/*
 * Copyright 2015 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.requestor.gwt;

import com.google.gwt.core.client.ScriptInjector;

/**
 * This class offers MD5 hashing.
 * </p>
 *
 * It's based on a fast MD5 implementation specific for client-side javascript and compiled with the closure compiler.
 * This implementation is injected as script and a global function called 'md5hash' is exposed.
 */
class MD5 {

    private static boolean injectionPending = true;

    public static String hash(String str) {
        if (injectionPending) {
            inject();
            injectionPending = false;
        }
        return hashNative(str);
    }

    private static native String hashNative(String str) /*-{
        return $wnd.md5hash(str);
    }-*/;

    private static void inject() {
        ScriptInjector.fromString("(function(){function n(f,d){var c=f[0],a=f[1],b=f[2],e=f[3],c=g(c,a,b,e,d[0],7," +
                "-680876936),e=g(e,c,a,b,d[1],12,-389564586),b=g(b,e,c,a,d[2],17,606105819),a=g(a,b,e,c,d[3],22," +
                "-1044525330),c=g(c,a,b,e,d[4],7,-176418897),e=g(e,c,a,b,d[5],12,1200080426),b=g(b,e,c,a,d[6],17," +
                "-1473231341),a=g(a,b,e,c,d[7],22,-45705983),c=g(c,a,b,e,d[8],7,1770035416),e=g(e,c,a,b,d[9],12," +
                "-1958414417),b=g(b,e,c,a,d[10],17,-42063),a=g(a,b,e,c,d[11],22,-1990404162),c=g(c,a,b,e,d[12],7," +
                "1804603682),e=g(e,c,a,b,d[13],12,-40341101),\n" +
                "b=g(b,e,c,a,d[14],17,-1502002290),a=g(a,b,e,c,d[15],22,1236535329),c=k(c,a,b,e,d[1],5,-165796510)," +
                "e=k(e,c,a,b,d[6],9,-1069501632),b=k(b,e,c,a,d[11],14,643717713),a=k(a,b,e,c,d[0],20,-373897302),c=k" +
                "(c,a,b,e,d[5],5,-701558691),e=k(e,c,a,b,d[10],9,38016083),b=k(b,e,c,a,d[15],14,-660478335),a=k(a,b," +
                "e,c,d[4],20,-405537848),c=k(c,a,b,e,d[9],5,568446438),e=k(e,c,a,b,d[14],9,-1019803690),b=k(b,e,c,a," +
                "d[3],14,-187363961),a=k(a,b,e,c,d[8],20,1163531501),c=k(c,a,b,e,d[13],5,-1444681467),e=k(e,c,a,b," +
                "d[2],9,-51403784),\n" +
                "b=k(b,e,c,a,d[7],14,1735328473),a=k(a,b,e,c,d[12],20,-1926607734),c=h(a^b^e,c,a,d[5],4,-378558),e=h" +
                "(c^a^b,e,c,d[8],11,-2022574463),b=h(e^c^a,b,e,d[11],16,1839030562),a=h(b^e^c,a,b,d[14],23,-35309556)" +
                ",c=h(a^b^e,c,a,d[1],4,-1530992060),e=h(c^a^b,e,c,d[4],11,1272893353),b=h(e^c^a,b,e,d[7],16," +
                "-155497632),a=h(b^e^c,a,b,d[10],23,-1094730640),c=h(a^b^e,c,a,d[13],4,681279174),e=h(c^a^b,e,c,d[0]," +
                "11,-358537222),b=h(e^c^a,b,e,d[3],16,-722521979),a=h(b^e^c,a,b,d[6],23,76029189),c=h(a^b^e,c,a,d[9]," +
                "4,-640364487),\n" +
                "e=h(c^a^b,e,c,d[12],11,-421815835),b=h(e^c^a,b,e,d[15],16,530742520),a=h(b^e^c,a,b,d[2],23," +
                "-995338651),c=l(c,a,b,e,d[0],6,-198630844),e=l(e,c,a,b,d[7],10,1126891415),b=l(b,e,c,a,d[14],15," +
                "-1416354905),a=l(a,b,e,c,d[5],21,-57434055),c=l(c,a,b,e,d[12],6,1700485571),e=l(e,c,a,b,d[3],10," +
                "-1894986606),b=l(b,e,c,a,d[10],15,-1051523),a=l(a,b,e,c,d[1],21,-2054922799),c=l(c,a,b,e,d[8],6," +
                "1873313359),e=l(e,c,a,b,d[15],10,-30611744),b=l(b,e,c,a,d[6],15,-1560198380),a=l(a,b,e,c,d[13],21," +
                "1309151649),c=l(c,a,b,e,\n" +
                "d[4],6,-145523070),e=l(e,c,a,b,d[11],10,-1120210379),b=l(b,e,c,a,d[2],15,718787259),a=l(a,b,e,c," +
                "d[9],21,-343485551);f[0]=m(c,f[0]);f[1]=m(a,f[1]);f[2]=m(b,f[2]);f[3]=m(e,f[3])}function h(f,d,c,a," +
                "b,e){d=m(m(d,f),m(a,e));return m(d<<b|d>>>32-b,c)}function g(f,d,c,a,b,e,g){return h(d&c|~d&a,f,d,b," +
                "e,g)}function k(f,d,c,a,b,e,g){return h(d&a|c&~a,f,d,b,e,g)}function l(f,d,c,a,b,e,g){return h(c^" +
                "(d|~a),f,d,b,e,g)}function p(f){var d=f;txt=\"\";var c=d.length;f=[1732584193,-271733879," +
                "-1732584194,271733878];\n" +
                "var a;for(a=64;a<=d.length;a+=64){for(var b=d.substring(a-64,a),e=[],g=void 0,g=0;64>g;g+=4)" +
                "e[g>>2]=b.charCodeAt(g)+(b.charCodeAt(g+1)<<8)+(b.charCodeAt(g+2)<<16)+(b.charCodeAt(g+3)<<24);n(f," +
                "e)}d=d.substring(a-64);b=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];for(a=0;a<d.length;a++)b[a>>2]|=d" +
                ".charCodeAt(a)<<(a%4<<3);b[a>>2]|=128<<(a%4<<3);if(55<a)for(n(f,b),a=0;16>a;a++)b[a]=0;b[14]=8*c;n" +
                "(f,b);for(d=0;d<f.length;d++){c=f[d];a=\"\";for(b=0;4>b;b++)a+=q[c>>8*b+4&15]+q[c>>8*b&15];" +
                "f[d]=a}return f.join(\"\")}function m(f,\n" +
                "d){return f+d&4294967295}var q=\"0123456789abcdef\".split(\"\");" +
                "\"5d41402abc4b2a76b9719d911017c592\"!=p(\"hello\")&&(m=function(f,d){var c=(f&65535)+(d&65535);" +
                "return(f>>16)+(d>>16)+(c>>16)<<16|c&65535});window.md5hash=p})()")
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();
    }
}
