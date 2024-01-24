package com.example.migeba.Utils;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

public class geotranslate {

    public static String[] temp;

    public static String FROMGEO(String input) {
        {
            input = FROMGEO2(input);
        }

        StringBuilder stt = new StringBuilder("");
        try {
            String[] temp;
            temp = input.split("");
            for (int i = 0; i < temp.length; i++)

                if (temp[i].equals("ა")) {
                    String etri = "a";
                    stt.append(etri);
                } else {
                    if (temp[i].equals("ბ")) {
                        String etri = "b";
                        stt.append(etri);
                    } else {
                        if (temp[i].equals("გ")) {
                            String etri = "g";
                            stt.append(etri);
                        } else {
                            if (temp[i].equals("დ")) {
                                String etri = "d";
                                stt.append(etri);
                            } else {
                                if (temp[i].equals("ე")) {
                                    String etri = "e";
                                    stt.append(etri);
                                } else {
                                    if (temp[i].equals("ვ")) {
                                        String etri = "v";
                                        stt.append(etri);
                                    } else {
                                        if (temp[i].equals("ზ")) {
                                            String etri = "z";
                                            stt.append(etri);
                                        } else {
                                            if (temp[i].equals("თ")) {
                                                String etri = "T";
                                                stt.append(etri);
                                            } else {
                                                if (temp[i].equals("ი")) {
                                                    String etri = "i";
                                                    stt.append(etri);
                                                } else {
                                                    if (temp[i].equals("კ")) {
                                                        String etri = "k";
                                                        stt.append(etri);
                                                    } else {
                                                        if (temp[i].equals("ლ")) {
                                                            String etri = "l";
                                                            stt.append(etri);
                                                        } else {
                                                            if (temp[i].equals("მ")) {
                                                                String etri = "m";
                                                                stt.append(etri);
                                                            } else {
                                                                if (temp[i].equals("ნ")) {
                                                                    String etri = "n";
                                                                    stt.append(etri);
                                                                } else {
                                                                    if (temp[i].equals("ო")) {
                                                                        String etri = "o";
                                                                        stt.append(etri);
                                                                    } else {
                                                                        if (temp[i].equals("პ")) {
                                                                            String etri = "p";
                                                                            stt.append(etri);
                                                                        } else {
                                                                            if (temp[i].equals("ჟ")) {
                                                                                String etri = "J";
                                                                                stt.append(etri);
                                                                            } else {
                                                                                if (temp[i].equals("რ")) {
                                                                                    String etri = "r";
                                                                                    stt.append(etri);
                                                                                } else {
                                                                                    if (temp[i].equals("ს")) {
                                                                                        String etri = "s";
                                                                                        stt.append(etri);
                                                                                    } else {
                                                                                        if (temp[i].equals("ტ")) {
                                                                                            String etri = "t";
                                                                                            stt.append(etri);
                                                                                        } else {
                                                                                            if (temp[i].equals("უ")) {
                                                                                                String etri = "u";
                                                                                                stt.append(etri);
                                                                                            } else {
                                                                                                if (temp[i].equals("ფ")) {
                                                                                                    String etri = "f";
                                                                                                    stt.append(etri);
                                                                                                } else {
                                                                                                    if (temp[i].equals("ქ")) {
                                                                                                        String etri = "q";
                                                                                                        stt.append(etri);
                                                                                                    } else {
                                                                                                        if (temp[i].equals("ღ")) {
                                                                                                            String etri = "R";
                                                                                                            stt.append(etri);
                                                                                                        } else {
                                                                                                            if (temp[i].equals("ყ")) {
                                                                                                                String etri = "y";
                                                                                                                stt.append(etri);
                                                                                                            } else {
                                                                                                                if (temp[i].equals("შ")) {
                                                                                                                    String etri = "S";
                                                                                                                    stt.append(etri);
                                                                                                                } else {
                                                                                                                    if (temp[i].equals("ჩ")) {
                                                                                                                        String etri = "C";
                                                                                                                        stt.append(etri);
                                                                                                                    } else {
                                                                                                                        if (temp[i].equals("ც")) {
                                                                                                                            String etri = "c";
                                                                                                                            stt.append(etri);
                                                                                                                        } else {
                                                                                                                            if (temp[i].equals("ძ")) {
                                                                                                                                String etri = "Z";
                                                                                                                                stt.append(etri);
                                                                                                                            } else {
                                                                                                                                if (temp[i].equals("წ")) {
                                                                                                                                    String etri = "w";
                                                                                                                                    stt.append(etri);
                                                                                                                                } else {
                                                                                                                                    if (temp[i].equals("ჭ")) {
                                                                                                                                        String etri = "W";
                                                                                                                                        stt.append(etri);
                                                                                                                                    } else {
                                                                                                                                        if (temp[i].equals("ხ")) {
                                                                                                                                            String etri = "x";
                                                                                                                                            stt.append(etri);
                                                                                                                                        } else {
                                                                                                                                            if (temp[i].equals("ჯ")) {
                                                                                                                                                String etri = "j";
                                                                                                                                                stt.append(etri);
                                                                                                                                            } else {
                                                                                                                                                if (temp[i].equals("ჰ")) {
                                                                                                                                                    String etri = "h";
                                                                                                                                                    stt.append(etri);
                                                                                                                                                } else {
                                                                                                                                                    if (temp[i].equals("#")) {
                                                                                                                                                        String etri = "N";
                                                                                                                                                        stt.append(etri);
                                                                                                                                                    } else {
                                                                                                                                                        if (temp[i].equals("'")) {
                                                                                                                                                            String etri = "";
                                                                                                                                                            stt.append(etri);
                                                                                                                                                        } else {
                                                                                                                                                            stt.append(temp[i]);
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


        } catch (Exception sjsddi) {
        }
        return stt.toString();
    }

    public static String TOGEO(String input) {

        StringBuilder stt = new StringBuilder("");
        try {

            char[] tempsa;
            tempsa = input.toCharArray();
            for (int i = 0; i < tempsa.length; i++) {
                String seaa = Character.toString(tempsa[i]);

                if (seaa.equals("a")) {
                    String etri = "ა";
                    stt.append(etri);
                } else {
                    if (seaa.equals("b")) {
                        String etri = "ბ";
                        stt.append(etri);
                    } else {
                        if (seaa.equals("g")) {
                            String etri = "გ";
                            stt.append(etri);
                        } else {
                            if (seaa.equals("d")) {
                                String etri = "დ";
                                stt.append(etri);
                            } else {
                                if (seaa.equals("e")) {
                                    String etri = "ე";
                                    stt.append(etri);
                                } else {
                                    if (seaa.equals("v")) {
                                        String etri = "ვ";
                                        stt.append(etri);
                                    } else {
                                        if (seaa.equals("z")) {
                                            String etri = "ზ";
                                            stt.append(etri);
                                        } else {
                                            if (seaa.equals("T")) {
                                                String etri = "თ";
                                                stt.append(etri);
                                            } else {
                                                if (seaa.equals("i")) {
                                                    String etri = "ი";
                                                    stt.append(etri);
                                                } else {
                                                    if (seaa.equals("k")) {
                                                        String etri = "კ";
                                                        stt.append(etri);
                                                    } else {
                                                        if (seaa.equals("l")) {
                                                            String etri = "ლ";
                                                            stt.append(etri);
                                                        } else {
                                                            if (seaa.equals("m")) {
                                                                String etri = "მ";
                                                                stt.append(etri);
                                                            } else {
                                                                if (seaa.equals("n")) {
                                                                    String etri = "ნ";
                                                                    stt.append(etri);
                                                                } else {
                                                                    if (seaa.equals("o")) {
                                                                        String etri = "ო";
                                                                        stt.append(etri);
                                                                    } else {
                                                                        if (seaa.equals("p")) {
                                                                            String etri = "პ";
                                                                            stt.append(etri);
                                                                        } else {
                                                                            if (seaa.equals("J")) {
                                                                                String etri = "ჟ";
                                                                                stt.append(etri);
                                                                            } else {
                                                                                if (seaa.equals("r")) {
                                                                                    String etri = "რ";
                                                                                    stt.append(etri);
                                                                                } else {
                                                                                    if (seaa.equals("s")) {
                                                                                        String etri = "ს";
                                                                                        stt.append(etri);
                                                                                    } else {
                                                                                        if (seaa.equals("t")) {
                                                                                            String etri = "ტ";
                                                                                            stt.append(etri);
                                                                                        } else {
                                                                                            if (seaa.equals("u")) {
                                                                                                String etri = "უ";
                                                                                                stt.append(etri);
                                                                                            } else {
                                                                                                if (seaa.equals("f")) {
                                                                                                    String etri = "ფ";
                                                                                                    stt.append(etri);
                                                                                                } else {
                                                                                                    if (seaa.equals("q")) {
                                                                                                        String etri = "ქ";
                                                                                                        stt.append(etri);
                                                                                                    } else {
                                                                                                        if (seaa.equals("R")) {
                                                                                                            String etri = "ღ";
                                                                                                            stt.append(etri);
                                                                                                        } else {
                                                                                                            if (seaa.equals("y")) {
                                                                                                                String etri = "ყ";
                                                                                                                stt.append(etri);
                                                                                                            } else {
                                                                                                                if (seaa.equals("S")) {
                                                                                                                    String etri = "შ";
                                                                                                                    stt.append(etri);
                                                                                                                } else {
                                                                                                                    if (seaa.equals("C")) {
                                                                                                                        String etri = "ჩ";
                                                                                                                        stt.append(etri);
                                                                                                                    } else {
                                                                                                                        if (seaa.equals("c")) {
                                                                                                                            String etri = "ც";
                                                                                                                            stt.append(etri);
                                                                                                                        } else {
                                                                                                                            if (seaa.equals("Z")) {
                                                                                                                                String etri = "ძ";
                                                                                                                                stt.append(etri);
                                                                                                                            } else {
                                                                                                                                if (seaa.equals("w")) {
                                                                                                                                    String etri = "წ";
                                                                                                                                    stt.append(etri);
                                                                                                                                } else {
                                                                                                                                    if (seaa.equals("W")) {
                                                                                                                                        String etri = "ჭ";
                                                                                                                                        stt.append(etri);
                                                                                                                                    } else {
                                                                                                                                        if (seaa.equals("x")) {
                                                                                                                                            String etri = "ხ";
                                                                                                                                            stt.append(etri);
                                                                                                                                        } else {
                                                                                                                                            if (seaa.equals("j")) {
                                                                                                                                                String etri = "ჯ";
                                                                                                                                                stt.append(etri);
                                                                                                                                            } else {
                                                                                                                                                if (seaa.equals("h")) {
                                                                                                                                                    String etri = "ჰ";
                                                                                                                                                    stt.append(etri);
                                                                                                                                                } else {
                                                                                                                                                    if (seaa.equals("#")) {
                                                                                                                                                        String etri = "N";
                                                                                                                                                        stt.append(etri);
                                                                                                                                                    } else {
                                                                                                                                                        if (seaa.equals("'")) {
                                                                                                                                                            String etri = "";
                                                                                                                                                            stt.append(etri);
                                                                                                                                                        } else {
                                                                                                                                                            if (seaa.equals("¤")) {
                                                                                                                                                                String etri = "T";
                                                                                                                                                                stt.append(etri);
                                                                                                                                                            } else {
                                                                                                                                                                if (seaa.equals("®")) {
                                                                                                                                                                    String etri = "J";
                                                                                                                                                                    stt.append(etri);
                                                                                                                                                                } else {
                                                                                                                                                                    if (seaa.equals("¦")) {
                                                                                                                                                                        String etri = "R";
                                                                                                                                                                        stt.append(etri);
                                                                                                                                                                    } else {
                                                                                                                                                                        if (seaa.equals("©")) {
                                                                                                                                                                            String etri = "S";
                                                                                                                                                                            stt.append(etri);
                                                                                                                                                                        } else {
                                                                                                                                                                            if (seaa.equals("«")) {
                                                                                                                                                                                String etri = "C";
                                                                                                                                                                                stt.append(etri);
                                                                                                                                                                            } else {
                                                                                                                                                                                if (seaa.equals("¬")) {
                                                                                                                                                                                    String etri = "Z";
                                                                                                                                                                                    stt.append(etri);
                                                                                                                                                                                } else {
                                                                                                                                                                                    if (seaa.equals("°")) {
                                                                                                                                                                                        String etri = "W";
                                                                                                                                                                                        stt.append(etri);
                                                                                                                                                                                    } else {
                                                                                                                                                                                        stt = stt.append(seaa);
                                                                                                                                                                                    }
                                                                                                                                                                                }
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

        } catch (Exception sjsddi) {
        }
        String TR = stt.toString();

//    {      TR=TOGEO2(TR);}

        return TR;
//return input;
    }

    public static String FROMGEO2(String input) {
        StringBuilder stt = new StringBuilder("");
        try {
            temp = input.split("");
            for (int i = 0; i < temp.length; i++) {


                String seaa = temp[i];
                if (seaa.equals("T")) {
                    String etri = "¤";
                    stt.append(etri);
                } else {
                    if (seaa.equals("J")) {
                        String etri = "®";
                        stt.append(etri);
                    } else {
                        if (seaa.equals("R")) {
                            String etri = "¦";
                            stt.append(etri);
                        } else {
                            if (seaa.equals("S")) {
                                String etri = "©";
                                stt.append(etri);
                            } else {
                                if (seaa.equals("C")) {
                                    String etri = "«";
                                    stt.append(etri);
                                } else {
                                    if (seaa.equals("Z")) {
                                        String etri = "¬";
                                        stt.append(etri);
                                    } else {
                                        if (seaa.equals("W")) {
                                            String etri = "°";
                                            stt.append(etri);


                                        } else {
                                            stt = stt.append(seaa);

                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }
        } catch (Exception sjsddi) {
        }
        Log.e(TAG, "FROMGEO2: " + stt.toString());
        return stt.toString();
    }

    public static String TOGEO2(String input) {
        StringBuilder stt = new StringBuilder("");
        try {
            temp = input.split("");
            for (int i = 0; i < temp.length; i++) {

                String seaa = temp[i];

                if (seaa.equals("¤")) {
                    String etri = "T";
                    stt.append(etri);
                } else {
                    if (seaa.equals("®")) {
                        String etri = "J";
                        stt.append(etri);
                    } else {
                        if (seaa.equals("¦")) {
                            String etri = "R";
                            stt.append(etri);
                        } else {
                            if (seaa.equals("©")) {
                                String etri = "S";
                                stt.append(etri);
                            } else {
                                if (seaa.equals("«")) {
                                    String etri = "C";
                                    stt.append(etri);
                                } else {
                                    if (seaa.equals("¬")) {
                                        String etri = "Z";
                                        stt.append(etri);
                                    } else {
                                        if (seaa.equals("°")) {
                                            String etri = "W";
                                            stt.append(etri);


                                        } else {
                                            stt = stt.append(seaa);

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } catch (Exception sjsddi) {
        }
        return stt.toString();
    }
}
