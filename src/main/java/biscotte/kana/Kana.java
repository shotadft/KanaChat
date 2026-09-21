package biscotte.kana;

public class Kana {
    private final String[][] r2kTable = {
            {"", "あ", "い", "う", "え", "お"},             // 0.
            {"k", "か", "き", "く", "け", "こ"},             // 1.
            {"s", "さ", "し", "す", "せ", "そ"},             // 2.
            {"t", "た", "ち", "つ", "て", "と"},             // 3.
            {"n", "な", "に", "ぬ", "ね", "の"},             // 4.
            {"h", "は", "ひ", "ふ", "へ", "ほ"},             // 5.
            {"m", "ま", "み", "む", "め", "も"},             // 6.
            {"y", "や", "い", "ゆ", "いぇ", "よ"},           // 7.
            {"r", "ら", "り", "る", "れ", "ろ"},             // 8.
            {"w", "わ", "うぃ", "う", "うぇ", "を"},         // 9.

            {"g", "が", "ぎ", "ぐ", "げ", "ご"},             //10.
            {"z", "ざ", "じ", "ず", "ぜ", "ぞ"},             //11.
            {"j", "じゃ", "じ", "じゅ", "じぇ", "じょ"},     //12.
            {"d", "だ", "ぢ", "づ", "で", "ど"},             //13.
            {"b", "ば", "び", "ぶ", "べ", "ぼ"},             //14.
            {"p", "ぱ", "ぴ", "ぷ", "ぺ", "ぽ"},             //15.
            {"gy", "ぎゃ", "ぎぃ", "ぎゅ", "ぎぇ", "ぎょ"},   //16.
            {"zy", "じゃ", "じぃ", "じゅ", "じぇ", "じょ"},   //17.
            {"jy", "じゃ", "じぃ", "じゅ", "じぇ", "じょ"},   //18.
            {"dh", "でゃ", "でぃ", "でゅ", "でぇ", "でょ"},   //19.
            {"dy", "ぢゃ", "ぢぃ", "ぢゅ", "ぢぇ", "ぢょ"},   //20.
            {"by", "びゃ", "びぃ", "びゅ", "びぇ", "びょ"},   //21.
            {"py", "ぴゃ", "ぴぃ", "ぴゅ", "ぴぇ", "ぴょ"},   //22.

            {"l", "ぁ", "ぃ", "ぅ", "ぇ", "ぉ"},             //23.
            {"v", "ヴぁ", "ヴぃ", "ヴ", "ヴぇ", "ヴぉ"},     //24.
            {"sh", "しゃ", "し", "しゅ", "しぇ", "しょ"},     //25.
            {"sy", "しゃ", "し", "しゅ", "しぇ", "しょ"},     //26.
            {"ch", "ちゃ", "ち", "ちゅ", "ちぇ", "ちょ"},     //27.
            {"cy", "ちゃ", "ち", "ちゅ", "ちぇ", "ちょ"},     //28.

            {"f", "ふぁ", "ふぃ", "ふ", "ふぇ", "ふぉ"},     //29.
            {"q", "くぁ", "くぃ", "く", "くぇ", "くぉ"},     //30.
            {"ky", "きゃ", "きぃ", "きゅ", "きぇ", "きょ"},   //31.
            {"ts", "つぁ", "つぃ", "つ", "つぇ", "つぉ"},     //32.
            {"ty", "ちゃ", "ちぃ", "ちゅ", "ちぇ", "ちょ"},   //33.
            {"ny", "にゃ", "にぃ", "にゅ", "にぇ", "にょ"},   //34.
            {"hy", "ひゃ", "ひぃ", "ひゅ", "ひぇ", "ひょ"},   //35.
            {"my", "みゃ", "みぃ", "みゅ", "みぇ", "みょ"},   //36.
            {"ry", "りゃ", "りぃ", "りゅ", "りぇ", "りょ"},   //37.
            {"ly", "ゃ", "ぃ", "ゅ", "ぇ", "ょ"},             //38.
            {"lt", "た", "ち", "っ", "て", "と"},             //39.
            {"xy", "ゃ", "ぃ", "ゅ", "ぇ", "ょ"},             //40.
            {"xt", "た", "ち", "っ", "て", "と"},             //41.
            {"x", "ぁ", "ぃ", "ぅ", "ぇ", "ぉ"},             //42.
    };
    private String srcLine;
    private String cnvLine;

    public Kana() {
        srcLine = "";
    }

    public String getLine() {
        return cnvLine;
    }

    public void setLine(String s) {
        srcLine = s; //s.toLowerCase();
    }

    private String R2K(String s, int n) {

        if (n < 5) {
            for (String[] strings : r2kTable) {
                if (s.equals(strings[0])) {
                    return strings[n + 1];
                }
            }
            return s + r2kTable[0][n + 1];
        } else if (n == 5) {
            return "ん";
        } else if (n == 6) {
            return "っ";
        } else {
            return "ー";
        }
    }

    public void convert() {
        String tmp;

        StringBuilder cnvBuf = new StringBuilder();
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < srcLine.length(); i++) {
            tmp = srcLine.substring(i, i + 1);

            switch (tmp) {
                case "a":
                    cnvBuf.append(R2K(buf.toString(), 0));
                    buf.setLength(0);
                    break;

                case "i":
                    cnvBuf.append(R2K(buf.toString(), 1));
                    buf.setLength(0);
                    break;

                case "u":
                    cnvBuf.append(R2K(buf.toString(), 2));
                    buf.setLength(0);
                    break;

                case "e":
                    cnvBuf.append(R2K(buf.toString(), 3));
                    buf.setLength(0);
                    break;

                case "o":
                    cnvBuf.append(R2K(buf.toString(), 4));
                    buf.setLength(0);
                    break;

                default:
                    if (buf.toString().equals("n")) {
                        if (!tmp.equals("y")) {          /* "ny" */
                            cnvBuf.append(R2K(buf.toString(), 5));
                            buf.setLength(0);

                            if (tmp.equals("n")) {
                                continue;                /* "nn" */
                            }
                        }
                    }

                    if (Character.isLetter(tmp.charAt(0))) {
                        if (buf.toString().equals(tmp)) {
                            cnvBuf.append(R2K(buf.toString(), 6)); /* "っ" */
                            buf.setLength(0);
                            buf.append(tmp);
                        } else {
                            buf.append(tmp);
                        }
                    } else {
                        if (tmp.equals("-")) {
                            tmp = R2K(buf.toString(), 7);
                        }

                        cnvBuf.append(buf);
                        cnvBuf.append(tmp);
                        buf.setLength(0);
                    }
                    break;
            }
        }

        // 最後の1文字が'n'ならば'ん'にする
        if (buf.toString().equals("n")) {
            cnvBuf.append(R2K(buf.toString(), 5));
            buf.setLength(0);
        }

        cnvBuf.append(buf);

        cnvLine = cnvBuf.toString();
    }
}
