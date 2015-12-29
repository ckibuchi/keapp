package com.rube.tt.keapp.xmpp;

/**
 * Created by rube on 6/7/15.
 */
public class XMPPFont {
        private String mFont = null;
        private String mColor = null;
        String mSize = null;

        public XMPPFont() {
        }

        public XMPPFont(String font) {
            mFont = font;
        }

        public XMPPFont(String font, String color) {
            mFont = font;
            mColor = color;
        }

        public String toString() {
            if (mFont != null && mColor == null) {
                return  "font-family:" + this.mFont;
            } else if (mFont != null && mColor != null) {
                return "font-family:" + this.mFont + "; " + "color:" + this.mColor;
            } else if (mFont == null && mColor == null) {
                return "font-family:null";
            } else {
                return "color:" + this.mColor;
            }
        }


}
