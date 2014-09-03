package de.fhg.iais.roberta.brick;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.ProjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import de.fhg.iais.roberta.ast.syntax.BrickConfiguration;
import de.fhg.iais.roberta.ast.transformer.JaxbTransformer;
import de.fhg.iais.roberta.blockly.generated.BlockSet;
import de.fhg.iais.roberta.codegen.lejos.AstToLejosJavaVisitor;
import de.fhg.iais.roberta.persistence.ProgramProcessor;
import de.fhg.iais.roberta.persistence.bo.Program;
import de.fhg.iais.roberta.persistence.connector.SessionWrapper;

public class CompilerWorkflow {

    private static final Logger LOG = LoggerFactory.getLogger(CompilerWorkflow.class);
    public static final String BASE_DIR = "../OpenRobertaRuntime/userProjects/"; // TODO: temporary path has to be be removed 

    /**
     * - load the program from the database<br>
     * - generate the AST<br>
     * - typecheck the AST, execute sanity checks, check a matching brick configuration<br>
     * - generate Java code<br>
     * - store the code in a token-specific (thus user-specific) directory<br>
     * - compile the code and generate a jar in the token-specific directory (use a ant script, will be replaced later)<br>
     * <b>Note:</b> the jar is prepared for upload, but not uploaded from here. After a handshake with the brick (the brick has to tell, that it is ready) the
     * jar is uploaded to the brick from another thread and then started on the brick
     * 
     * @param session to retrieve the the program from the database
     * @param token the credential the end user (at the terminal) and the brick have both agreed to use
     * @param projectName to retrieve the program code
     * @param programName to retrieve the program code
     * @param configurationName the hardware configuration that is expected to have been used when assembling the brick
     * @return a message in case of an error; null otherwise
     */
    public static String execute(SessionWrapper session, String token, String projectName, String programName, String configurationName) {
        Program program = new ProgramProcessor().getProgram(session, projectName, programName);
        if ( program == null ) {
            return "program not found";
        }
        String blocklyXml = program.getProgramText();
        if ( blocklyXml == null ) {
            return "program has no blocks";
        }
        blocklyXml = blocklyXml.replaceAll("http://www.w3.org/1999/xhtml", "http://de.fhg.iais.roberta.blockly");
        blocklyXml =
            blocklyXml
                .replaceFirst("<instance x=\"\\d+\" y=\"\\d+\"><block type=\"robControls_start\" id=\"\\d+\" deletable=\"false\"></block></instance>", "");
        BrickConfiguration brickConfiguration = new BrickConfiguration.Builder().build();
        JaxbTransformer<Void> transformer;
        try {
            transformer = generateTransformer(blocklyXml);
        } catch ( Exception e ) {
            return "blocks could not be transformed (message: " + e.getMessage() + ")";
        }
        String javaCode = AstToLejosJavaVisitor.generate(programName, brickConfiguration, transformer.getTree(), true);
        LOG.info("to be compiled: {}", javaCode);
        try {
            storeGeneratedProgram(token, programName, javaCode);
        } catch ( Exception e ) {
            return "generated java code could not be stored (message: + " + e.getMessage() + ")";
        }
        runBuild(BASE_DIR, token, programName, "generated.main");
        return null;
    }

    /**
     * return the jaxb transformer for a given program test.
     * 
     * @param blocklyXml the blockly XML as String
     * @return jaxb the transformer
     * @throws Exception
     */
    private static JaxbTransformer<Void> generateTransformer(String blocklyXml) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(BlockSet.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        InputStream stream = new ByteArrayInputStream(blocklyXml.getBytes(StandardCharsets.UTF_8));
        InputSource src = new InputSource(stream);
        BlockSet project = (BlockSet) jaxbUnmarshaller.unmarshal(src);

        JaxbTransformer<Void> transformer = new JaxbTransformer<>();
        transformer.projectToAST(project);
        return transformer;
    }

    private static void storeGeneratedProgram(String token, String programName, String javaCode) throws Exception {
        File javaFile = new File(BASE_DIR + token + "/src/" + programName + ".java");
        FileUtils.writeStringToFile(javaFile, javaCode, StandardCharsets.UTF_8.displayName());
    }

    /**
     * 1. Make target folder (if not exists).<br>
     * 2. Clean target folder (everything inside).<br>
     * 3. Compile .java files to .class.<br>
     * 4. Make jar from class files and add META-INF entries.<br>
     * 
     * @param userProjectsDir
     * @param token
     * @param mainFile
     * @param mainPackage
     */
    private static void runBuild(String userProjectsDir, String token, String mainFile, String mainPackage) {

        File buildFile = new File(userProjectsDir + "/build.xml");
        org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();

        project.setProperty("user.projects.dir", userProjectsDir);
        project.setProperty("token.dir", token);
        project.setProperty("main.name", mainFile);
        project.setProperty("main.package", mainPackage);

        project.init();

        ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
        projectHelper.parse(project, buildFile);

        project.executeTarget(project.getDefaultTarget());
    }

}