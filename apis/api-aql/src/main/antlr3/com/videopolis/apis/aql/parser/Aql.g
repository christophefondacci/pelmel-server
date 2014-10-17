grammar Aql;

options {
  language = Java;
}

@header {
  package com.videopolis.apis.aql.parser;
  
  import com.videopolis.apis.aql.handler.AqlHandler;
  import com.videopolis.apis.aql.helper.AqlParsingHelper;
}

@lexer::header {
  package com.videopolis.apis.aql.parser;
}

@members {

  private AqlHandler aqlHandler;
  
  public void setAqlHandler(AqlHandler aqlHandler) {
    this.aqlHandler = aqlHandler;
  }
}

query
  : 'get' t=type { aqlHandler.beginGet(t); } selectorClause? withClause? forClause? { aqlHandler.endGet(); }
  ;
  
type returns [String name]
  : n=(IDENTIFIER | CLASS_NAME) { name = $n.text; }
  ;
  
selectorClause
  : uniqueKeyClause
  | alternateKeyClause
  ;
  
uniqueKeyClause
  : 'unique' 'key' i=(IDENTIFIER | INTEGER) { aqlHandler.uniqueKeySelector($i.text); }
  ;
  
alternateKeyClause
  : 'alternate' 'key' i=IDENTIFIER { aqlHandler.alternateKeySelector(
                                        AqlParsingHelper.extractItemType($i.text),
                                        AqlParsingHelper.extractItemId($i.text)); }
  ;
  
withClause
  : 'with' { aqlHandler.beginWith(); } withList { aqlHandler.endWith(); }
  ;
  
withList
  : withCriterion (',' withCriterion)*
  ;
  
withCriterion
  : { aqlHandler.beginWithCriterion(); } withCriterionContent { aqlHandler.endWithCriterion(); }
  ;
  
withCriterionContent
  : t=type { aqlHandler.addWith(t); } paginationClause? withClause? 
  | '(' t=type { aqlHandler.addWith(t); } paginationClause? withClause? ')' 
  | 'nearest' t=type 'radius' r=INTEGER { aqlHandler.addWithNearest(t, Double.valueOf($r.text)); } paginationClause withClause?
  | '(' 'nearest' t=type 'radius' r=INTEGER { aqlHandler.addWithNearest(t, Double.valueOf($r.text)); } paginationClause withClause? ')'
  ;
  
paginationClause
  : 'page' p=INTEGER 'size' s=INTEGER { aqlHandler.addPagination(Integer.valueOf($s.text), Integer.valueOf($p.text)); }
  ;
  
forClause
  : 'for' i=IDENTIFIER { aqlHandler.beginFor(AqlParsingHelper.extractItemType($i.text), AqlParsingHelper.extractItemId($i.text)); } paginationClause { aqlHandler.endFor(); }
  ;

fragment LETTER : ('a'..'z' | 'A'..'Z') ;
fragment DIGIT : '0'..'9';

INTEGER: DIGIT+ ;
IDENTIFIER: LETTER (LETTER | DIGIT)*;
CLASS_NAME: (LETTER (LETTER | DIGIT)* '.')+ LETTER (LETTER | DIGIT)*;
WS: (' ' | '\t' | '\n' | '\r' | '\f')+ { $channel = HIDDEN; };