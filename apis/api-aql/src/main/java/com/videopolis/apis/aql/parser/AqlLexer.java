// $ANTLR 3.1.3 Mar 17, 2009 19:23:44 com/videopolis/apis/aql/parser/Aql.g 2010-12-07 16:32:45

  package com.videopolis.apis.aql.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AqlLexer extends Lexer {
    public static final int INTEGER=6;
    public static final int CLASS_NAME=5;
    public static final int LETTER=7;
    public static final int T__22=22;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int EOF=-1;
    public static final int T__19=19;
    public static final int WS=9;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int IDENTIFIER=4;
    public static final int T__13=13;
    public static final int T__10=10;
    public static final int DIGIT=8;

    // delegates
    // delegators

    public AqlLexer() {;} 
    public AqlLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public AqlLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "com/videopolis/apis/aql/parser/Aql.g"; }

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:11:7: ( 'get' )
            // com/videopolis/apis/aql/parser/Aql.g:11:9: 'get'
            {
            match("get"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:12:7: ( 'unique' )
            // com/videopolis/apis/aql/parser/Aql.g:12:9: 'unique'
            {
            match("unique"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:13:7: ( 'key' )
            // com/videopolis/apis/aql/parser/Aql.g:13:9: 'key'
            {
            match("key"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:14:7: ( 'alternate' )
            // com/videopolis/apis/aql/parser/Aql.g:14:9: 'alternate'
            {
            match("alternate"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:15:7: ( 'with' )
            // com/videopolis/apis/aql/parser/Aql.g:15:9: 'with'
            {
            match("with"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:16:7: ( ',' )
            // com/videopolis/apis/aql/parser/Aql.g:16:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:17:7: ( '(' )
            // com/videopolis/apis/aql/parser/Aql.g:17:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:18:7: ( ')' )
            // com/videopolis/apis/aql/parser/Aql.g:18:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:19:7: ( 'nearest' )
            // com/videopolis/apis/aql/parser/Aql.g:19:9: 'nearest'
            {
            match("nearest"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:20:7: ( 'radius' )
            // com/videopolis/apis/aql/parser/Aql.g:20:9: 'radius'
            {
            match("radius"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:21:7: ( 'page' )
            // com/videopolis/apis/aql/parser/Aql.g:21:9: 'page'
            {
            match("page"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:22:7: ( 'size' )
            // com/videopolis/apis/aql/parser/Aql.g:22:9: 'size'
            {
            match("size"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:23:7: ( 'for' )
            // com/videopolis/apis/aql/parser/Aql.g:23:9: 'for'
            {
            match("for"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "LETTER"
    public final void mLETTER() throws RecognitionException {
        try {
            // com/videopolis/apis/aql/parser/Aql.g:77:17: ( ( 'a' .. 'z' | 'A' .. 'Z' ) )
            // com/videopolis/apis/aql/parser/Aql.g:77:19: ( 'a' .. 'z' | 'A' .. 'Z' )
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "LETTER"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // com/videopolis/apis/aql/parser/Aql.g:78:16: ( '0' .. '9' )
            // com/videopolis/apis/aql/parser/Aql.g:78:18: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:80:8: ( ( DIGIT )+ )
            // com/videopolis/apis/aql/parser/Aql.g:80:10: ( DIGIT )+
            {
            // com/videopolis/apis/aql/parser/Aql.g:80:10: ( DIGIT )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // com/videopolis/apis/aql/parser/Aql.g:80:10: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:81:11: ( LETTER ( LETTER | DIGIT )* )
            // com/videopolis/apis/aql/parser/Aql.g:81:13: LETTER ( LETTER | DIGIT )*
            {
            mLETTER(); 
            // com/videopolis/apis/aql/parser/Aql.g:81:20: ( LETTER | DIGIT )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||(LA2_0>='a' && LA2_0<='z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // com/videopolis/apis/aql/parser/Aql.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "CLASS_NAME"
    public final void mCLASS_NAME() throws RecognitionException {
        try {
            int _type = CLASS_NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:82:11: ( ( LETTER ( LETTER | DIGIT )* '.' )+ LETTER ( LETTER | DIGIT )* )
            // com/videopolis/apis/aql/parser/Aql.g:82:13: ( LETTER ( LETTER | DIGIT )* '.' )+ LETTER ( LETTER | DIGIT )*
            {
            // com/videopolis/apis/aql/parser/Aql.g:82:13: ( LETTER ( LETTER | DIGIT )* '.' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                alt4 = dfa4.predict(input);
                switch (alt4) {
            	case 1 :
            	    // com/videopolis/apis/aql/parser/Aql.g:82:14: LETTER ( LETTER | DIGIT )* '.'
            	    {
            	    mLETTER(); 
            	    // com/videopolis/apis/aql/parser/Aql.g:82:21: ( LETTER | DIGIT )*
            	    loop3:
            	    do {
            	        int alt3=2;
            	        int LA3_0 = input.LA(1);

            	        if ( ((LA3_0>='0' && LA3_0<='9')||(LA3_0>='A' && LA3_0<='Z')||(LA3_0>='a' && LA3_0<='z')) ) {
            	            alt3=1;
            	        }


            	        switch (alt3) {
            	    	case 1 :
            	    	    // com/videopolis/apis/aql/parser/Aql.g:
            	    	    {
            	    	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	    	        input.consume();

            	    	    }
            	    	    else {
            	    	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	    	        recover(mse);
            	    	        throw mse;}


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop3;
            	        }
            	    } while (true);

            	    match('.'); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

            mLETTER(); 
            // com/videopolis/apis/aql/parser/Aql.g:82:52: ( LETTER | DIGIT )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='0' && LA5_0<='9')||(LA5_0>='A' && LA5_0<='Z')||(LA5_0>='a' && LA5_0<='z')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // com/videopolis/apis/aql/parser/Aql.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLASS_NAME"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com/videopolis/apis/aql/parser/Aql.g:83:3: ( ( ' ' | '\\t' | '\\n' | '\\r' | '\\f' )+ )
            // com/videopolis/apis/aql/parser/Aql.g:83:5: ( ' ' | '\\t' | '\\n' | '\\r' | '\\f' )+
            {
            // com/videopolis/apis/aql/parser/Aql.g:83:5: ( ' ' | '\\t' | '\\n' | '\\r' | '\\f' )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>='\t' && LA6_0<='\n')||(LA6_0>='\f' && LA6_0<='\r')||LA6_0==' ') ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // com/videopolis/apis/aql/parser/Aql.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);

             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // com/videopolis/apis/aql/parser/Aql.g:1:8: ( T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | INTEGER | IDENTIFIER | CLASS_NAME | WS )
        int alt7=17;
        alt7 = dfa7.predict(input);
        switch (alt7) {
            case 1 :
                // com/videopolis/apis/aql/parser/Aql.g:1:10: T__10
                {
                mT__10(); 

                }
                break;
            case 2 :
                // com/videopolis/apis/aql/parser/Aql.g:1:16: T__11
                {
                mT__11(); 

                }
                break;
            case 3 :
                // com/videopolis/apis/aql/parser/Aql.g:1:22: T__12
                {
                mT__12(); 

                }
                break;
            case 4 :
                // com/videopolis/apis/aql/parser/Aql.g:1:28: T__13
                {
                mT__13(); 

                }
                break;
            case 5 :
                // com/videopolis/apis/aql/parser/Aql.g:1:34: T__14
                {
                mT__14(); 

                }
                break;
            case 6 :
                // com/videopolis/apis/aql/parser/Aql.g:1:40: T__15
                {
                mT__15(); 

                }
                break;
            case 7 :
                // com/videopolis/apis/aql/parser/Aql.g:1:46: T__16
                {
                mT__16(); 

                }
                break;
            case 8 :
                // com/videopolis/apis/aql/parser/Aql.g:1:52: T__17
                {
                mT__17(); 

                }
                break;
            case 9 :
                // com/videopolis/apis/aql/parser/Aql.g:1:58: T__18
                {
                mT__18(); 

                }
                break;
            case 10 :
                // com/videopolis/apis/aql/parser/Aql.g:1:64: T__19
                {
                mT__19(); 

                }
                break;
            case 11 :
                // com/videopolis/apis/aql/parser/Aql.g:1:70: T__20
                {
                mT__20(); 

                }
                break;
            case 12 :
                // com/videopolis/apis/aql/parser/Aql.g:1:76: T__21
                {
                mT__21(); 

                }
                break;
            case 13 :
                // com/videopolis/apis/aql/parser/Aql.g:1:82: T__22
                {
                mT__22(); 

                }
                break;
            case 14 :
                // com/videopolis/apis/aql/parser/Aql.g:1:88: INTEGER
                {
                mINTEGER(); 

                }
                break;
            case 15 :
                // com/videopolis/apis/aql/parser/Aql.g:1:96: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 16 :
                // com/videopolis/apis/aql/parser/Aql.g:1:107: CLASS_NAME
                {
                mCLASS_NAME(); 

                }
                break;
            case 17 :
                // com/videopolis/apis/aql/parser/Aql.g:1:118: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA4 dfa4 = new DFA4(this);
    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA4_eotS =
        "\1\uffff\1\2\1\uffff\1\2\1\uffff";
    static final String DFA4_eofS =
        "\5\uffff";
    static final String DFA4_minS =
        "\1\101\1\56\1\uffff\1\56\1\uffff";
    static final String DFA4_maxS =
        "\2\172\1\uffff\1\172\1\uffff";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\1\uffff\1\1";
    static final String DFA4_specialS =
        "\5\uffff}>";
    static final String[] DFA4_transitionS = {
            "\32\1\6\uffff\32\1",
            "\1\4\1\uffff\12\3\7\uffff\32\3\6\uffff\32\3",
            "",
            "\1\4\1\uffff\12\3\7\uffff\32\3\6\uffff\32\3",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "()+ loopback of 82:13: ( LETTER ( LETTER | DIGIT )* '.' )+";
        }
    }
    static final String DFA7_eotS =
        "\1\uffff\5\22\3\uffff\5\22\1\uffff\1\22\1\uffff\1\22\1\uffff\1\22"+
        "\1\uffff\11\22\1\50\1\22\1\52\6\22\1\61\1\uffff\1\22\1\uffff\1\22"+
        "\1\64\2\22\1\67\1\70\1\uffff\2\22\1\uffff\2\22\2\uffff\1\75\2\22"+
        "\1\100\1\uffff\1\22\1\102\1\uffff\1\22\1\uffff\1\104\1\uffff";
    static final String DFA7_eofS =
        "\105\uffff";
    static final String DFA7_minS =
        "\1\11\5\56\3\uffff\5\56\1\uffff\1\56\1\uffff\1\56\1\uffff\1\56\1"+
        "\uffff\23\56\1\uffff\1\56\1\uffff\6\56\1\uffff\2\56\1\uffff\2\56"+
        "\2\uffff\4\56\1\uffff\2\56\1\uffff\1\56\1\uffff\1\56\1\uffff";
    static final String DFA7_maxS =
        "\6\172\3\uffff\5\172\1\uffff\1\172\1\uffff\1\172\1\uffff\1\172\1"+
        "\uffff\23\172\1\uffff\1\172\1\uffff\6\172\1\uffff\2\172\1\uffff"+
        "\2\172\2\uffff\4\172\1\uffff\2\172\1\uffff\1\172\1\uffff\1\172\1"+
        "\uffff";
    static final String DFA7_acceptS =
        "\6\uffff\1\6\1\7\1\10\5\uffff\1\16\1\uffff\1\21\1\uffff\1\17\1\uffff"+
        "\1\20\23\uffff\1\1\1\uffff\1\3\6\uffff\1\15\2\uffff\1\5\2\uffff"+
        "\1\13\1\14\4\uffff\1\2\2\uffff\1\12\1\uffff\1\11\1\uffff\1\4";
    static final String DFA7_specialS =
        "\105\uffff}>";
    static final String[] DFA7_transitionS = {
            "\2\20\1\uffff\2\20\22\uffff\1\20\7\uffff\1\7\1\10\2\uffff\1"+
            "\6\3\uffff\12\16\7\uffff\32\17\6\uffff\1\4\4\17\1\15\1\1\3\17"+
            "\1\3\2\17\1\11\1\17\1\13\1\17\1\12\1\14\1\17\1\2\1\17\1\5\3"+
            "\17",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\21\25\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\15\23\1\25\14\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\26\25\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\13\23\1\27\16\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\10\23\1\30\21\23",
            "",
            "",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\31\25\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\1\32\31\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\1\33\31\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\10\23\1\34\21\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\16\23\1\35\13\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\23\23\1\36\6\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\10\23\1\37\21\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\30\23\1\40\1\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\23\23\1\41\6\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\23\23\1\42\6\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\1\43\31\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\3\23\1\44\26\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\6\23\1\45\23\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\31\23\1\46",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\21\23\1\47\10\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\20\23\1\51\11\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\53\25\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\7\23\1\54\22\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\21\23\1\55\10\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\10\23\1\56\21\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\57\25\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\60\25\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\24\23\1\62\5\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\21\23\1\63\10\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\65\25\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\24\23\1\66\5\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\71\25\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\15\23\1\72\14\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\22\23\1\73\7\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\22\23\1\74\7\23",
            "",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\1\76\31\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\23\23\1\77\6\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\23\23\1\101\6\23",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\4\23\1\103\25\23",
            "",
            "\1\24\1\uffff\12\23\7\uffff\32\23\6\uffff\32\23",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | INTEGER | IDENTIFIER | CLASS_NAME | WS );";
        }
    }
 

}