// $ANTLR 3.1.3 Mar 17, 2009 19:23:44 com/videopolis/apis/aql/parser/Aql.g 2010-12-07 16:32:45

  package com.videopolis.apis.aql.parser;
  
  import com.videopolis.apis.aql.handler.AqlHandler;
  import com.videopolis.apis.aql.helper.AqlParsingHelper;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AqlParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "CLASS_NAME", "INTEGER", "LETTER", "DIGIT", "WS", "'get'", "'unique'", "'key'", "'alternate'", "'with'", "','", "'('", "')'", "'nearest'", "'radius'", "'page'", "'size'", "'for'"
    };
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
    public static final int T__13=13;
    public static final int IDENTIFIER=4;
    public static final int T__10=10;
    public static final int DIGIT=8;

    // delegates
    // delegators


        public AqlParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public AqlParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return AqlParser.tokenNames; }
    public String getGrammarFileName() { return "com/videopolis/apis/aql/parser/Aql.g"; }



      private AqlHandler aqlHandler;
      
      public void setAqlHandler(AqlHandler aqlHandler) {
        this.aqlHandler = aqlHandler;
      }



    // $ANTLR start "query"
    // com/videopolis/apis/aql/parser/Aql.g:27:1: query : 'get' t= type ( selectorClause )? ( withClause )? ( forClause )? ;
    public final void query() throws RecognitionException {
        String t = null;


        try {
            // com/videopolis/apis/aql/parser/Aql.g:28:3: ( 'get' t= type ( selectorClause )? ( withClause )? ( forClause )? )
            // com/videopolis/apis/aql/parser/Aql.g:28:5: 'get' t= type ( selectorClause )? ( withClause )? ( forClause )?
            {
            match(input,10,FOLLOW_10_in_query47); 
            pushFollow(FOLLOW_type_in_query51);
            t=type();

            state._fsp--;

             aqlHandler.beginGet(t); 
            // com/videopolis/apis/aql/parser/Aql.g:28:46: ( selectorClause )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==11||LA1_0==13) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // com/videopolis/apis/aql/parser/Aql.g:28:46: selectorClause
                    {
                    pushFollow(FOLLOW_selectorClause_in_query55);
                    selectorClause();

                    state._fsp--;


                    }
                    break;

            }

            // com/videopolis/apis/aql/parser/Aql.g:28:62: ( withClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==14) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // com/videopolis/apis/aql/parser/Aql.g:28:62: withClause
                    {
                    pushFollow(FOLLOW_withClause_in_query58);
                    withClause();

                    state._fsp--;


                    }
                    break;

            }

            // com/videopolis/apis/aql/parser/Aql.g:28:74: ( forClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==22) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // com/videopolis/apis/aql/parser/Aql.g:28:74: forClause
                    {
                    pushFollow(FOLLOW_forClause_in_query61);
                    forClause();

                    state._fsp--;


                    }
                    break;

            }

             aqlHandler.endGet(); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "query"


    // $ANTLR start "type"
    // com/videopolis/apis/aql/parser/Aql.g:31:1: type returns [String name] : n= ( IDENTIFIER | CLASS_NAME ) ;
    public final String type() throws RecognitionException {
        String name = null;

        Token n=null;

        try {
            // com/videopolis/apis/aql/parser/Aql.g:32:3: (n= ( IDENTIFIER | CLASS_NAME ) )
            // com/videopolis/apis/aql/parser/Aql.g:32:5: n= ( IDENTIFIER | CLASS_NAME )
            {
            n=(Token)input.LT(1);
            if ( (input.LA(1)>=IDENTIFIER && input.LA(1)<=CLASS_NAME) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

             name = (n!=null?n.getText():null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return name;
    }
    // $ANTLR end "type"


    // $ANTLR start "selectorClause"
    // com/videopolis/apis/aql/parser/Aql.g:35:1: selectorClause : ( uniqueKeyClause | alternateKeyClause );
    public final void selectorClause() throws RecognitionException {
        try {
            // com/videopolis/apis/aql/parser/Aql.g:36:3: ( uniqueKeyClause | alternateKeyClause )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==11) ) {
                alt4=1;
            }
            else if ( (LA4_0==13) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // com/videopolis/apis/aql/parser/Aql.g:36:5: uniqueKeyClause
                    {
                    pushFollow(FOLLOW_uniqueKeyClause_in_selectorClause108);
                    uniqueKeyClause();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // com/videopolis/apis/aql/parser/Aql.g:37:5: alternateKeyClause
                    {
                    pushFollow(FOLLOW_alternateKeyClause_in_selectorClause114);
                    alternateKeyClause();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "selectorClause"


    // $ANTLR start "uniqueKeyClause"
    // com/videopolis/apis/aql/parser/Aql.g:40:1: uniqueKeyClause : 'unique' 'key' i= ( IDENTIFIER | INTEGER ) ;
    public final void uniqueKeyClause() throws RecognitionException {
        Token i=null;

        try {
            // com/videopolis/apis/aql/parser/Aql.g:41:3: ( 'unique' 'key' i= ( IDENTIFIER | INTEGER ) )
            // com/videopolis/apis/aql/parser/Aql.g:41:5: 'unique' 'key' i= ( IDENTIFIER | INTEGER )
            {
            match(input,11,FOLLOW_11_in_uniqueKeyClause129); 
            match(input,12,FOLLOW_12_in_uniqueKeyClause131); 
            i=(Token)input.LT(1);
            if ( input.LA(1)==IDENTIFIER||input.LA(1)==INTEGER ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

             aqlHandler.uniqueKeySelector((i!=null?i.getText():null)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "uniqueKeyClause"


    // $ANTLR start "alternateKeyClause"
    // com/videopolis/apis/aql/parser/Aql.g:44:1: alternateKeyClause : 'alternate' 'key' i= IDENTIFIER ;
    public final void alternateKeyClause() throws RecognitionException {
        Token i=null;

        try {
            // com/videopolis/apis/aql/parser/Aql.g:45:3: ( 'alternate' 'key' i= IDENTIFIER )
            // com/videopolis/apis/aql/parser/Aql.g:45:5: 'alternate' 'key' i= IDENTIFIER
            {
            match(input,13,FOLLOW_13_in_alternateKeyClause158); 
            match(input,12,FOLLOW_12_in_alternateKeyClause160); 
            i=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_alternateKeyClause164); 
             aqlHandler.alternateKeySelector(
                                                    AqlParsingHelper.extractItemType((i!=null?i.getText():null)),
                                                    AqlParsingHelper.extractItemId((i!=null?i.getText():null))); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "alternateKeyClause"


    // $ANTLR start "withClause"
    // com/videopolis/apis/aql/parser/Aql.g:50:1: withClause : 'with' withList ;
    public final void withClause() throws RecognitionException {
        try {
            // com/videopolis/apis/aql/parser/Aql.g:51:3: ( 'with' withList )
            // com/videopolis/apis/aql/parser/Aql.g:51:5: 'with' withList
            {
            match(input,14,FOLLOW_14_in_withClause181); 
             aqlHandler.beginWith(); 
            pushFollow(FOLLOW_withList_in_withClause185);
            withList();

            state._fsp--;

             aqlHandler.endWith(); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "withClause"


    // $ANTLR start "withList"
    // com/videopolis/apis/aql/parser/Aql.g:54:1: withList : withCriterion ( ',' withCriterion )* ;
    public final void withList() throws RecognitionException {
        try {
            // com/videopolis/apis/aql/parser/Aql.g:55:3: ( withCriterion ( ',' withCriterion )* )
            // com/videopolis/apis/aql/parser/Aql.g:55:5: withCriterion ( ',' withCriterion )*
            {
            pushFollow(FOLLOW_withCriterion_in_withList202);
            withCriterion();

            state._fsp--;

            // com/videopolis/apis/aql/parser/Aql.g:55:19: ( ',' withCriterion )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==15) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // com/videopolis/apis/aql/parser/Aql.g:55:20: ',' withCriterion
            	    {
            	    match(input,15,FOLLOW_15_in_withList205); 
            	    pushFollow(FOLLOW_withCriterion_in_withList207);
            	    withCriterion();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "withList"


    // $ANTLR start "withCriterion"
    // com/videopolis/apis/aql/parser/Aql.g:58:1: withCriterion : withCriterionContent ;
    public final void withCriterion() throws RecognitionException {
        try {
            // com/videopolis/apis/aql/parser/Aql.g:59:3: ( withCriterionContent )
            // com/videopolis/apis/aql/parser/Aql.g:59:5: withCriterionContent
            {
             aqlHandler.beginWithCriterion(); 
            pushFollow(FOLLOW_withCriterionContent_in_withCriterion226);
            withCriterionContent();

            state._fsp--;

             aqlHandler.endWithCriterion(); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "withCriterion"


    // $ANTLR start "withCriterionContent"
    // com/videopolis/apis/aql/parser/Aql.g:62:1: withCriterionContent : (t= type ( paginationClause )? ( withClause )? | '(' t= type ( paginationClause )? ( withClause )? ')' | 'nearest' t= type 'radius' r= INTEGER paginationClause ( withClause )? | '(' 'nearest' t= type 'radius' r= INTEGER paginationClause ( withClause )? ')' );
    public final void withCriterionContent() throws RecognitionException {
        Token r=null;
        String t = null;


        try {
            // com/videopolis/apis/aql/parser/Aql.g:63:3: (t= type ( paginationClause )? ( withClause )? | '(' t= type ( paginationClause )? ( withClause )? ')' | 'nearest' t= type 'radius' r= INTEGER paginationClause ( withClause )? | '(' 'nearest' t= type 'radius' r= INTEGER paginationClause ( withClause )? ')' )
            int alt12=4;
            switch ( input.LA(1) ) {
            case IDENTIFIER:
            case CLASS_NAME:
                {
                alt12=1;
                }
                break;
            case 16:
                {
                int LA12_2 = input.LA(2);

                if ( (LA12_2==18) ) {
                    alt12=4;
                }
                else if ( ((LA12_2>=IDENTIFIER && LA12_2<=CLASS_NAME)) ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 2, input);

                    throw nvae;
                }
                }
                break;
            case 18:
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // com/videopolis/apis/aql/parser/Aql.g:63:5: t= type ( paginationClause )? ( withClause )?
                    {
                    pushFollow(FOLLOW_type_in_withCriterionContent245);
                    t=type();

                    state._fsp--;

                     aqlHandler.addWith(t); 
                    // com/videopolis/apis/aql/parser/Aql.g:63:39: ( paginationClause )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==20) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // com/videopolis/apis/aql/parser/Aql.g:63:39: paginationClause
                            {
                            pushFollow(FOLLOW_paginationClause_in_withCriterionContent249);
                            paginationClause();

                            state._fsp--;


                            }
                            break;

                    }

                    // com/videopolis/apis/aql/parser/Aql.g:63:57: ( withClause )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==14) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // com/videopolis/apis/aql/parser/Aql.g:63:57: withClause
                            {
                            pushFollow(FOLLOW_withClause_in_withCriterionContent252);
                            withClause();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // com/videopolis/apis/aql/parser/Aql.g:64:5: '(' t= type ( paginationClause )? ( withClause )? ')'
                    {
                    match(input,16,FOLLOW_16_in_withCriterionContent260); 
                    pushFollow(FOLLOW_type_in_withCriterionContent264);
                    t=type();

                    state._fsp--;

                     aqlHandler.addWith(t); 
                    // com/videopolis/apis/aql/parser/Aql.g:64:43: ( paginationClause )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==20) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // com/videopolis/apis/aql/parser/Aql.g:64:43: paginationClause
                            {
                            pushFollow(FOLLOW_paginationClause_in_withCriterionContent268);
                            paginationClause();

                            state._fsp--;


                            }
                            break;

                    }

                    // com/videopolis/apis/aql/parser/Aql.g:64:61: ( withClause )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==14) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // com/videopolis/apis/aql/parser/Aql.g:64:61: withClause
                            {
                            pushFollow(FOLLOW_withClause_in_withCriterionContent271);
                            withClause();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,17,FOLLOW_17_in_withCriterionContent274); 

                    }
                    break;
                case 3 :
                    // com/videopolis/apis/aql/parser/Aql.g:65:5: 'nearest' t= type 'radius' r= INTEGER paginationClause ( withClause )?
                    {
                    match(input,18,FOLLOW_18_in_withCriterionContent281); 
                    pushFollow(FOLLOW_type_in_withCriterionContent285);
                    t=type();

                    state._fsp--;

                    match(input,19,FOLLOW_19_in_withCriterionContent287); 
                    r=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_withCriterionContent291); 
                     aqlHandler.addWithNearest(t, Double.valueOf((r!=null?r.getText():null))); 
                    pushFollow(FOLLOW_paginationClause_in_withCriterionContent295);
                    paginationClause();

                    state._fsp--;

                    // com/videopolis/apis/aql/parser/Aql.g:65:117: ( withClause )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==14) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // com/videopolis/apis/aql/parser/Aql.g:65:117: withClause
                            {
                            pushFollow(FOLLOW_withClause_in_withCriterionContent297);
                            withClause();

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // com/videopolis/apis/aql/parser/Aql.g:66:5: '(' 'nearest' t= type 'radius' r= INTEGER paginationClause ( withClause )? ')'
                    {
                    match(input,16,FOLLOW_16_in_withCriterionContent304); 
                    match(input,18,FOLLOW_18_in_withCriterionContent306); 
                    pushFollow(FOLLOW_type_in_withCriterionContent310);
                    t=type();

                    state._fsp--;

                    match(input,19,FOLLOW_19_in_withCriterionContent312); 
                    r=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_withCriterionContent316); 
                     aqlHandler.addWithNearest(t, Double.valueOf((r!=null?r.getText():null))); 
                    pushFollow(FOLLOW_paginationClause_in_withCriterionContent320);
                    paginationClause();

                    state._fsp--;

                    // com/videopolis/apis/aql/parser/Aql.g:66:121: ( withClause )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==14) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // com/videopolis/apis/aql/parser/Aql.g:66:121: withClause
                            {
                            pushFollow(FOLLOW_withClause_in_withCriterionContent322);
                            withClause();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,17,FOLLOW_17_in_withCriterionContent325); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "withCriterionContent"


    // $ANTLR start "paginationClause"
    // com/videopolis/apis/aql/parser/Aql.g:69:1: paginationClause : 'page' p= INTEGER 'size' s= INTEGER ;
    public final void paginationClause() throws RecognitionException {
        Token p=null;
        Token s=null;

        try {
            // com/videopolis/apis/aql/parser/Aql.g:70:3: ( 'page' p= INTEGER 'size' s= INTEGER )
            // com/videopolis/apis/aql/parser/Aql.g:70:5: 'page' p= INTEGER 'size' s= INTEGER
            {
            match(input,20,FOLLOW_20_in_paginationClause340); 
            p=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_paginationClause344); 
            match(input,21,FOLLOW_21_in_paginationClause346); 
            s=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_paginationClause350); 
             aqlHandler.addPagination(Integer.valueOf((s!=null?s.getText():null)), Integer.valueOf((p!=null?p.getText():null))); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "paginationClause"


    // $ANTLR start "forClause"
    // com/videopolis/apis/aql/parser/Aql.g:73:1: forClause : 'for' i= IDENTIFIER paginationClause ;
    public final void forClause() throws RecognitionException {
        Token i=null;

        try {
            // com/videopolis/apis/aql/parser/Aql.g:74:3: ( 'for' i= IDENTIFIER paginationClause )
            // com/videopolis/apis/aql/parser/Aql.g:74:5: 'for' i= IDENTIFIER paginationClause
            {
            match(input,22,FOLLOW_22_in_forClause367); 
            i=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_forClause371); 
             aqlHandler.beginFor(AqlParsingHelper.extractItemType((i!=null?i.getText():null)), AqlParsingHelper.extractItemId((i!=null?i.getText():null))); 
            pushFollow(FOLLOW_paginationClause_in_forClause375);
            paginationClause();

            state._fsp--;

             aqlHandler.endFor(); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "forClause"

    // Delegated rules


 

    public static final BitSet FOLLOW_10_in_query47 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_type_in_query51 = new BitSet(new long[]{0x0000000000406802L});
    public static final BitSet FOLLOW_selectorClause_in_query55 = new BitSet(new long[]{0x0000000000404002L});
    public static final BitSet FOLLOW_withClause_in_query58 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_forClause_in_query61 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_type85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_uniqueKeyClause_in_selectorClause108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alternateKeyClause_in_selectorClause114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_uniqueKeyClause129 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_uniqueKeyClause131 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_set_in_uniqueKeyClause135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_alternateKeyClause158 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_alternateKeyClause160 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_alternateKeyClause164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_withClause181 = new BitSet(new long[]{0x0000000000050030L});
    public static final BitSet FOLLOW_withList_in_withClause185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_withCriterion_in_withList202 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_15_in_withList205 = new BitSet(new long[]{0x0000000000050030L});
    public static final BitSet FOLLOW_withCriterion_in_withList207 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_withCriterionContent_in_withCriterion226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_withCriterionContent245 = new BitSet(new long[]{0x0000000000104002L});
    public static final BitSet FOLLOW_paginationClause_in_withCriterionContent249 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_withClause_in_withCriterionContent252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_withCriterionContent260 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_type_in_withCriterionContent264 = new BitSet(new long[]{0x0000000000124000L});
    public static final BitSet FOLLOW_paginationClause_in_withCriterionContent268 = new BitSet(new long[]{0x0000000000024000L});
    public static final BitSet FOLLOW_withClause_in_withCriterionContent271 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_withCriterionContent274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_withCriterionContent281 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_type_in_withCriterionContent285 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_withCriterionContent287 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INTEGER_in_withCriterionContent291 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_paginationClause_in_withCriterionContent295 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_withClause_in_withCriterionContent297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_withCriterionContent304 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_withCriterionContent306 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_type_in_withCriterionContent310 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_withCriterionContent312 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INTEGER_in_withCriterionContent316 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_paginationClause_in_withCriterionContent320 = new BitSet(new long[]{0x0000000000024000L});
    public static final BitSet FOLLOW_withClause_in_withCriterionContent322 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_withCriterionContent325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_paginationClause340 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INTEGER_in_paginationClause344 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_paginationClause346 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INTEGER_in_paginationClause350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_forClause367 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_forClause371 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_paginationClause_in_forClause375 = new BitSet(new long[]{0x0000000000000002L});

}