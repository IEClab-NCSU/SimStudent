package tracer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import java.util.regex.Pattern;

import tracer.MTException.MTExceptionType;
import jess.*;

/**
 * Userfunction called to retrieve hints from a production rule
 * @author Alex Xiao
 *
 */
public class HintFunction extends MTUserFunction {

	private int hintLevel = -1;
	
	public void resetHint() {
		this.hintLevel = -1;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException, MTException {
		Value v = super.call(vv, context);
		if (this.on) {
			List<String> hints = parseHints(vv, context);
			if (hints == null || hints.size() == 0)
				throw new MTException(MTExceptionType.NOHINT, "");
			if (this.hintLevel < hints.size() - 1)
				this.hintLevel++;
			throw new MTException(MTExceptionType.HINT, hints.get(this.hintLevel));
		}
		return v;
	}
	
	private List<String> parseHints(ValueVector vv, Context context) throws JessException {
		List<String> hints = new ArrayList<String>();
		int length = vv.size();
		String message = "";
		for (int i = 1; i < length; i++) {
			message += getStringFromValue(vv.get(i), context);
		}
		// matches all groups with [*], where * is the hint
		Pattern p = Pattern.compile("\\[(.+?)\\]");
		Matcher m = p.matcher(message);
		
		while(m.find()) {
			String group = m.group(1);
			hints.add(group);
		}

		return hints;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "construct-message";
	}

}
