/**
 * @license
 * Visual Blocks Editor
 *
 * Copyright 2012 Google Inc.
 * https://blockly.googlecode.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @fileoverview Object representing a error.
 * @author fraser@google.com (Neil Fraser)
 */
'use strict';

goog.provide('Blockly.Error');

goog.require('Blockly.Bubble');
goog.require('Blockly.Icon');

/**
 * Class for a error.
 * 
 * @param {!Blockly.Block}
 *            block The block associated with this error.
 * @extends {Blockly.Icon}
 * @constructor
 */
Blockly.Error = function(block) {
    Blockly.Error.superClass_.constructor.call(this, block);
    this.createIcon_();
};
goog.inherits(Blockly.Error, Blockly.Icon);

/**
 * Create the text for the error's bubble.
 * 
 * @param {string}
 *            text The text to display.
 * @return {!SVGTextElement} The top-level node of the text.
 * @private
 */
Blockly.Error.textToDom_ = function(text) {
    var paragraph = /** @type {!SVGTextElement} */(Blockly.createSvgElement('text', {
        'class' : 'blocklyText blocklyBubbleText',
        'y' : Blockly.Bubble.BORDER_WIDTH
    }, null));
    var lines = text.split('\n');
    for (var i = 0; i < lines.length; i++) {
        var tspanElement = Blockly.createSvgElement('tspan', {
            'dy' : '1em',
            'x' : Blockly.Bubble.BORDER_WIDTH
        }, paragraph);
        var textNode = document.createTextNode(lines[i]);
        tspanElement.appendChild(textNode);
    }
    return paragraph;
};

/**
 * Error text (if bubble is not visible).
 * 
 * @private
 */
Blockly.Error.prototype.text_ = '';

/**
 * Create the icon on the block.
 * 
 * @private
 */
Blockly.Error.prototype.createIcon_ = function() {
    Blockly.Icon.prototype.createIcon_.call(this);
    /*
     * Here's the markup that will be generated: <path class="blocklyIconShield"
     * d="..."/> <text class="blocklyIconMark" x="8" y="13">!</text>
     */
    var iconShield = Blockly.createSvgElement('polygon', {
        'class' : 'blocklyIconShield blocklyIconShieldError',
        'points' : '4,0 11,0 15,4 15,11 11,15 4,15 0,11 0,4'
    }, this.iconGroup_);
    this.iconMark_ = Blockly.createSvgElement('text', {
        'class' : 'blocklyIconMark  blocklyIconMarkWarningError',
        'x' : Blockly.Icon.RADIUS - 0.25,
        'y' : 2 * Blockly.Icon.RADIUS - 4
    }, this.iconGroup_);
    this.iconMark_.appendChild(document.createTextNode('\u00D7'));
};

/**
 * Show or hide the error bubble.
 * 
 * @param {boolean}
 *            visible True if the bubble should be visible.
 */
Blockly.Error.prototype.setVisible = function(visible) {
    if (visible == this.isVisible()) {
        // No change.
        return;
    }
    var text = this.getText();
    var size = this.getBubbleSize();
    if (visible) {
        // Create the bubble.
        var paragraph = Blockly.Error.textToDom_(this.text_);
        this.bubble_ = new Blockly.Bubble(
        /** @type {!Blockly.Workspace} */ (this.block_.workspace), paragraph, this.block_.svg_.svgPath_, this.iconX_, this.iconY_, null, null);
        if (Blockly.RTL) {
            // Right-align the paragraph.
            // This cannot be done until the bubble is rendered on screen.
            var maxWidth = paragraph.getBBox().width;
            for (var x = 0, textElement; textElement = paragraph.childNodes[x]; x++) {
                textElement.setAttribute('text-anchor', 'end');
                textElement.setAttribute('x', maxWidth + Blockly.Bubble.BORDER_WIDTH);
            }
        }
        this.updateColour('#E2001A');
        // Bump the error into the right location.
        var size = this.bubble_.getBubbleSize();
        this.bubble_.setBubbleSize(size.width, size.height);
    } else {
        // Dispose of the bubble.
        this.bubble_.dispose();
        this.bubble_ = null;
        this.body_ = null;
    }
//Restore the bubble stats after the visibility switch.
    this.setText(text);
    this.setBubbleSize(size.width, size.height);
};

/**
 * Bring the error to the top of the stack when clicked on.
 * 
 * @param {!Event}
 *            e Mouse up event.
 * @private
 */
Blockly.Error.prototype.bodyFocus_ = function(e) {
    this.bubble_.promote_();
};

/**
 * Get the dimensions of this comment's bubble.
 * 
 * @return {!Object} Object with width and height properties.
 */
Blockly.Error.prototype.getBubbleSize = function() {
    if (this.isVisible()) {
        return this.bubble_.getBubbleSize();
    } else {
        return {
            width : this.width_,
            height : this.height_
        };
    }
};

/**
 * Size this comment's bubble.
 * 
 * @param {number}
 *            width Width of the bubble.
 * @param {number}
 *            height Height of the bubble.
 */
Blockly.Error.prototype.setBubbleSize = function(width, height) {
    if (this.textarea_) {
        this.bubble_.setBubbleSize(width, height);
    } else {
        this.width_ = width;
        this.height_ = height;
    }
};

/**
 * Returns this comment's text.
 * 
 * @return {string} Comment text.
 */
Blockly.Error.prototype.getText = function() {
    return this.textarea_ ? this.textarea_.value : this.text_;
};

/**
 * Set this error's text.
 * 
 * @param {string}
 *            text Error text.
 */
Blockly.Error.prototype.setText = function(text) {
    if (this.text_ == text) {
        return;
    }
    this.text_ = text;
    if (this.isVisible()) {
        this.setVisible(false);
        this.setVisible(true);
    }
};

/**
 * Dispose of this error.
 */
Blockly.Error.prototype.dispose = function() {
    this.block_.error = null;
    Blockly.Icon.prototype.dispose.call(this);
};
