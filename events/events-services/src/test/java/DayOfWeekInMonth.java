import java.util.Calendar;

public class DayOfWeekInMonth {

	public static void main(String[] args) {

		Calendar ca1 = Calendar.getInstance();

		ca1.set(2012, 6, 7);

		int DAY_OF_WEEK_IN_MONTH = ca1.get(Calendar.DAY_OF_WEEK_IN_MONTH);

		// More Calendar Date option can check
		/*
		 * int DAY_OF_MONTH=ca1.get(Calendar.DAY_OF_MONTH); int
		 * DAY_OF_WEEK=ca1.get(Calendar.DAY_OF_WEEK); int
		 * DAY_OF_YEAR=ca1.get(Calendar.DAY_OF_YEAR); int
		 * WEEK_OF_MONTH=ca1.get(Calendar.WEEK_OF_MONTH); int
		 * WEEK_OF_YEAR=ca1.get(Calendar.WEEK_OF_YEAR);
		 */

		System.out.println("DAY OF WEEK IN MONTH :" + DAY_OF_WEEK_IN_MONTH);
	}
}