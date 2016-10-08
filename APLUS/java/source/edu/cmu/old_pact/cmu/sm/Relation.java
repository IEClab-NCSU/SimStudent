package edu.cmu.old_pact.cmu.sm;


/*The Relation class is used to encapsulate the fact that side-order
  is irrelevant.  This is used by SymbolManipulator to determine
  whether two inequalities or equations are equal.  Although this does
  handle equations, it shouldn't be confused with the Equation class,
  which does other things.  The two classes should be brought together
  at some point.*/

public class Relation /*implements Queryable*/{
	/*the five relations we support: =, <, <=, >, >= (=< and => are
      also okay, of course)*/
	public static final int EQ  = 0;
	public static final int LT  = 1;
	public static final int LTE = 2;
	public static final int GT  = 3;
	public static final int GTE = 4;

	public static final String relString[] = {"=","<","<=",">",">="};

	Expression leftside, rightside;
	int rel = -1;

	public Relation(int r,Expression left,Expression right){
		rel = r;
		leftside = left;
		rightside = right;
	}

	public Relation(String r) throws BadExpressionError{
		parseRelation(r,null,false);
	}

	public Relation(String r,String[] vars) throws BadExpressionError{
		parseRelation(r,vars,true);
	}

	protected void parseRelation(String r,String[] vars,boolean useVars) throws BadExpressionError{
		try{
			SymbolManipulator sm = new SymbolManipulator();
			int relPos,relLen,x;

			/*first locate and identify the relation operator*/
			relPos = r.indexOf('=');
			if(relPos > 0){
				/* =, >=, or <= */
				x = r.indexOf('>');
				if(x > 0){
					/* >= */
					rel = GTE;
					relLen = 2;
					if(x < relPos){
						relPos = x;
					}
				}
				else{
					x = r.indexOf('<');
					if(x > 0){
						/* <= */
						rel = LTE;
						relLen = 2;
						if(x < relPos){
							relPos = x;
						}
					}
					else{
						/* = */
						rel = EQ;
						relLen = 1;
					}
				}
			}
			else{
				/* < or > */
				relLen = 1;
				relPos = r.indexOf('>');
				if(relPos > 0){
					rel = GT;
				}
				else{
					relPos = r.indexOf('<');
					if(relPos > 0){
						rel = LT;
					}
				}
			}

			if(rel == -1){
				throw new BadExpressionError(r);
			}

			/*now split up the relation around the operator and parse
              each side*/
			String leftString = r.substring(0,relPos);
			String rightString = r.substring(relPos+relLen);
			if(useVars){
				leftside = sm.parse(leftString,vars);
				rightside = sm.parse(rightString,vars);
			}
			else{
				leftside = sm.parse(leftString);
				rightside = sm.parse(rightString);
			}
		}
		catch (ParseException err) {
			throw new BadExpressionError(r);
		}
		catch (TokenMgrError err) {
			throw new BadExpressionError(r);
		}
	}

	public int getType(){
		return rel;
	}

	public Expression getLeft(){
		return leftside;
	}

	public Expression getRight(){
		return rightside;
	}

	/**/
	public boolean matches(Relation r2){
		int r2type = r2.getType();
		Expression r2left = r2.getLeft();
		Expression r2right = r2.getRight();
		if(r2left == null || r2right == null || r2type == -1 || rel == -1){
			return false;
		}

		boolean ret = false;
		switch(rel){
		case EQ:
			if(r2type == EQ){
				ret = sidesMatch(r2left,r2right,true);
			}
			break;
		case LT:
			if(r2type == LT){
				ret = sidesMatch(r2left,r2right,false);
			}
			else if(r2type == GT){
				ret = sidesMatch(r2right,r2left,false);
			}
			break;
		case LTE:
			if(r2type == LTE){
				ret = sidesMatch(r2left,r2right,false);
			}
			else if(r2type == GTE){
				ret = sidesMatch(r2right,r2left,false);
			}
			break;
		case GT:
			if(r2type == GT){
				ret = sidesMatch(r2left,r2right,false);
			}
			else if(r2type == LT){
				ret = sidesMatch(r2right,r2left,false);
			}
			break;
		case GTE:
			if(r2type == GTE){
				ret = sidesMatch(r2left,r2right,false);
			}
			else if(r2type == LTE){
				ret = sidesMatch(r2right,r2left,false);
			}
			break;
		}

		return ret;
	}

	public boolean sidesMatch(Expression r2left,Expression r2right,boolean allowSwap){
		boolean rr,ll,lr=false,rl=false;
		ll = leftside.algebraicEqual(r2left);
		rr = rightside.algebraicEqual(r2right);
		if(allowSwap){
			lr = leftside.algebraicEqual(r2right);
			rl = rightside.algebraicEqual(r2left);
		}
		return ((ll && rr) || (allowSwap && lr && rl));
	}

	public String toString(){
		if(rel >= 0 && leftside != null && rightside != null){
			return leftside.toString() + relString[rel] + rightside.toString();
		}
		else{
			return "null relation";
		}
	}

	public String debugForm(){
		if(rel >= 0 && leftside != null && rightside != null){
			return leftside.debugForm() + relString[rel] + rightside.debugForm();
		}
		else{
			return "null relation";
		}
	}
}
