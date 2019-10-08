/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ga4gh.discovery.search.beaconnetwork;

import io.debezium.ddl.parser.mysql.generated.MySqlParser;
import io.debezium.ddl.parser.mysql.generated.MySqlParserBaseListener;
import lombok.extern.log4j.Log4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mfiume
 */
@Log4j
public class BeaconQueryParser extends MySqlParserBaseListener {

    private static final Logger logger = LoggerFactory.getLogger(BeaconQueryParser.class);
    
    private final BeaconQuery beaconQuery;

    public BeaconQueryParser() {
        this.beaconQuery = new BeaconQuery();
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        //System.out.println("Entered\n" + ctx.getText() + "\n" + ctx.getClass() + "\n\n");
    }
    
    @Override
    public void enterFromClause(MySqlParser.FromClauseContext ctx) {
        
        logger.info("Parsing from clause " + ctx.getText());
        
        parseExpressionContext(ctx.whereExpr);
        MySqlParser.ExpressionContext ectx = ctx.whereExpr;
        for (int i = 0; i < ectx.getChildCount(); i++) {
            this.parseExpressionContext(ectx.getChild(i));
        }
        
        if (beaconQuery.getBeaconId() == null) {
            usage("Required field beaconId not specified");
        } else if (beaconQuery.getChromosome() == null) {
            usage("Required field chromosome not specified");
        } else if (beaconQuery.getPosition() == null) {
            usage("Required field position not specified");
        } else if (beaconQuery.getAllele() == null) {
            usage("Required field allele not specified");
        } else if (beaconQuery.getReference() == null) {
            usage("Required field reference not specified");
        }
    }

    private void usage() {
        usage(null);
    }

    private void usage(String prefix) {

        String msg = "Usage: query must be in the form `SELECT exists FROM <table_name> WHERE beaconId = <beaconId> AND chromsome = <chromosome> AND position = <position> AND allele = <allele> AND reference = <reference>`";

        if (prefix != null) {
            msg = prefix + "; " + msg;
        }

        throw new UnsupportedOperationException(msg);
    }

    public boolean isValid() {
        return beaconQuery.getBeaconId() != null
                && beaconQuery.getChromosome() != null
                && beaconQuery.getPosition() != null
                && beaconQuery.getAllele() != null
                && beaconQuery.getReference() != null;
    }

    public BeaconQuery getBeaconQuery() {
        return beaconQuery;
    }

    private String unquote(String text) {
        for (String delim : new String[]{"\"", "\'"}) {
            if (text.startsWith(delim)) {
                text = text.replaceAll("^" + delim + "|" + delim + "$", "");
                break;
            }
        }
        return text;
    }

    private BeaconNetworkSearchAdapter.PROPERTIES lookupPropertyByKey(String keyStr) {
        for (BeaconNetworkSearchAdapter.PROPERTIES property : BeaconNetworkSearchAdapter.PROPERTIES.values()) {
            if (property.toString().equals(keyStr)) {
                return property;
            }
        }
        return null;
    }

    private void parseExpressionContext(ParseTree expr) {
        
        logger.info("Parsing expression " + expr.getText());
        
        if (expr instanceof MySqlParser.PredicateExpressionContext) {
            parsePredicateExpressionContext((MySqlParser.PredicateExpressionContext) expr);
        } else if (expr instanceof MySqlParser.LogicalOperatorContext) {
            parseLogicalOperatorContext((MySqlParser.LogicalOperatorContext) expr);
        } else {
            for (int i = 0; i < expr.getChildCount(); i++) {
                this.parseExpressionContext(expr.getChild(i));
            }
        }
    }

    private void parsePredicateExpressionContext(MySqlParser.PredicateExpressionContext pectx) {
        
        logger.info("Parsing predicate expression " + pectx.getText());
        
        MySqlParser.PredicateContext predicate = pectx.predicate();

        if (predicate.getChildCount() == 1) { 
            parseExpressionContext(pectx.getChild(0));
            return;
        }

        logger.info("Predicate has " + predicate.getChildCount() + " children");
        
        // assume binary operation
        if (predicate.getChildCount() == 3) {

            // check for the equality operator
            ParseTree operator = predicate.getChild(1);
            if (!(operator instanceof MySqlParser.ComparisonOperatorContext && operator.getText().equals("="))) {
                usage("Uknown operator " + operator.getText());
            }

            // assume the key is on the left and value is on the left
            int indexOfKey = 0;
            int indexOfValue = 2;

            ParseTree key = predicate.getChild(indexOfKey);
            ParseTree value = predicate.getChild(indexOfValue);

            // check they're atoms
            if (!(key instanceof MySqlParser.ExpressionAtomPredicateContext && value instanceof MySqlParser.ExpressionAtomPredicateContext)) {
                usage("Unparsable predicate expression " + predicate.getText());
            }

            String keyStr = unquote(key.getText());
            String valStr = unquote(value.getText());

            try {

                BeaconNetworkSearchAdapter.PROPERTIES property = lookupPropertyByKey(keyStr);

                switch (property) {
                    case VARIANT_CHROMOSOME:
                        beaconQuery.setChromosome(valStr);
                        break;
                    case VARIANT_ALLELE:
                        beaconQuery.setAllele(valStr);
                        break;
                    case VARIANT_POSITION:
                        beaconQuery.setPosition(Long.parseLong(valStr));
                        break;
                    case VARIANT_REFERENCE:
                        beaconQuery.setReference(valStr);
                        break;
                    case VARIANT_BEACONID:
                        beaconQuery.setBeaconId(valStr);
                        break;
                    case VARIANT_REFERENCE_ALLELE:
                        beaconQuery.setReferenceAllele(valStr);
                        break;
                    default:
                        usage("Unknown key " + keyStr);
                }
                
                logger.info("Parsed property " + property);

            } catch (IllegalArgumentException ex) {
                usage("Unparsable expression " + predicate.getText());
            }
        } else {
            logger.info("Not parsing predicate expression " + predicate.getText());
        }
        
    }

    private void parseLogicalOperatorContext(MySqlParser.LogicalOperatorContext expr) {
        
        logger.info("Parsing logical operator " + expr.getText());
        
        MySqlParser.LogicalOperatorContext lectx = (MySqlParser.LogicalOperatorContext) expr;
        if (!lectx.getText().toLowerCase().equals("and")) {
            usage("Invalid logical operator " + lectx.getText());
        }
    }

}

/**
 * 
 * (dmlStatement 
 *  (selectStatement 
 *      (querySpecification SELECT 
 *          (selectElements *) 
 *          (fromClause FROM 
 *              (tableSources 
 *                  (tableSource 
 *                      (tableSourceItem 
 *                          (tableName 
 *                              (fullId 
 *                                  (uid 
 *                                      (simpleId variants)
 *                                  )
 *                              )
 *                          )
 *                      )
 *                  )
 *              ) 
 *              WHERE 
 *              (expression 
 *                  (predicate 
 *                      (predicate 
 *                          (expressionAtom (constant (stringLiteral "chromosome")))
 *                      ) 
 *                      (comparisonOperator =) 
 *                      (predicate 
 *                          (expressionAtom (constant (stringLiteral "chr19")))
 *                      )
 *                  )
 *              )
 *          ) 
 *          (limitClause LIMIT (decimalLiteral 50))
 *      )
 *  )
 * )

 * 
 */
