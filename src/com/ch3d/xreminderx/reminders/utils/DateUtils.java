package com.ch3d.xreminderx.reminders.utils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;

import com.ch3d.xreminderx.R;

public class DateUtils
{
	private static class LocaleDateFormat extends SimpleDateFormat
	{
		public LocaleDateFormat(final Context context, final int format)
		{
			super(context.getString(format));
		}
	}

	// TODO: Use SparseArray
	private final static Map<Integer, SimpleDateFormat>	dateFormats				= new HashMap<Integer, SimpleDateFormat>();

	private final static String							THIS_FILE				= "DateUtils";

	public static final int								SECOND					= 1000;

	public static final int								MINUTE					= 60 * SECOND;

	public static final int								HOUR					= 60 * MINUTE;

	public static final int								FULL_TIME_DAY			= 24 * HOUR;

	public static final long							FULL_TIME_WEEK			= 7 * FULL_TIME_DAY;

	public static final GregorianCalendar				calendar				= new GregorianCalendar();

	public static final SimpleDateFormat				dateFormatter4filename	= new SimpleDateFormat("HH-mm MM-dd-yy");

	// private static String DATE_FORMAT_MONTH_DAY = "MM/dd";

	// private static String DATE_FORMAT_YEAR_MONTH_DAY = "yyyy/MM/dd";

	// public static final String DATE_FORMAT_MONTH_DAY_TIME = "MM/dd kk:mm";

	// public static final SimpleDateFormat dateFormatterMD = new SimpleDateFormat(DATE_FORMAT_MONTH_DAY);

	// public static final SimpleDateFormat dateFormatterYMD = new SimpleDateFormat(
	// DATE_FORMAT_YEAR_MONTH_DAY);

	// public static final SimpleDateFormat dateFormatterMDT = new SimpleDateFormat(DATE_FORMAT_MONTH_DAY_TIME);

	public static SimpleDateFormat						customDateFormatter		= new SimpleDateFormat();

	private static String								customDateFormat;

	private static long									delta;

	private static DateFormatSymbols					dfs						= new DateFormatSymbols(
																						Locale.getDefault());

	private static String								weekdays[]				= dfs.getWeekdays();

	public static TimeZone findTimeZone(final String abbreviature)
	{
		for(final String id : TimeZone.getAvailableIDs())
		{
			final TimeZone tz = TimeZone.getTimeZone(id);
			if(tz.getDisplayName(false, TimeZone.SHORT).equals(abbreviature))
			{
				return tz;
			}
			if(tz.getDisplayName(true, TimeZone.SHORT).equals(abbreviature))
			{
				return tz;
			}
		}
		return null;
	}

	// public static String format(final Context context, final Date date)
	// {
	// return getDateFormat(context, R.string.date_format).format(date);
	// }

	// public static String format(final Context context, final long date)
	// {
	// return format(context, new Date(date));
	// }
	//
	// public static String format(final Context context, final long date, final String format)
	// {
	// return format(context, new Date(date));
	// }

	public static String format(final Date date, final String format)
	{
		assert format != null;
		if(!customDateFormat.equals(format))
		{
			customDateFormat = format;
			customDateFormatter.applyPattern(customDateFormat);
		}
		return customDateFormatter.format(date);
	}

	public static String format4filename(final Date date)
	{
		return dateFormatter4filename.format(date);
	}

	public static String formatShortTime(final long time)
	{
		final Date currentTime = new Date();
		final Date timeOfCall = new Date(time);
		final long dif = currentTime.getTime() - time;

		if(DateUtils.isSameDay(currentTime, timeOfCall))
		{
			// today: 12pm
			return formatTime_todayAction(time);
		}
		else if(dif < DateUtils.FULL_TIME_WEEK)
		{
			// this week: Mon 12pm
			return formatTime_thisWeekAction(time);
		}
		else if(isSameYear(time))
		{
			// this year: Mar 12
			return formatTime_thisYearAction(time);
		}
		else
		{
			return formatTime_otherYearAction(time);
		}
	}

	public static String formatTime_otherYearAction(final long time)
	{
		return DateUtils.format(new Date(time), "MMM yyyy");
	}

	public static String formatTime_thisWeekAction(final long time)
	{
		return format(new Date(time), "EEE h:mm a");
	}

	public static String formatTime_thisYearAction(final long time)
	{
		return DateUtils.format(new Date(time), "MMM d");
	}

	public static String formatTime_todayAction(final long time)
	{
		return format(new Date(time), "kk:mm");
	}

	public static final int getCalendarDay()
	{
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public static final int getCalendarYear()
	{
		return calendar.get(Calendar.YEAR);
	}

	public static Integer getCurrentYear()
	{
		synchronized(calendar)
		{
			calendar.setTimeInMillis(System.currentTimeMillis());
			return getCalendarYear();
		}
	}

	/**
	 * @param context
	 *            - Current context
	 * @param ts
	 *            - timestamp
	 * @return Date in one of the following format: <li>Today</li> <li>Yesterday</li> <li>Monday</li> <li>Friday</li>
	 *         <li>Jul 1</li>
	 */
	public static String getDate(final Context context, final long ts)
	{
		final Calendar now = Calendar.getInstance();
		final Calendar data = Calendar.getInstance();
		data.setTimeInMillis(ts);
		if(now.get(Calendar.YEAR) != data.get(Calendar.YEAR))
		{
			return getDateFormat(context, R.string.date_format_year_month_day).format(data.getTime());
		}
		if(now.get(Calendar.MONTH) != data.get(Calendar.MONTH))
		{
			return getDateFormat(context, R.string.date_format_month_day).format(data.getTime());
		}
		if(now.get(Calendar.DAY_OF_MONTH) == data.get(Calendar.DAY_OF_MONTH))
		{
			return context.getString(R.string.today);
		}
		if((now.get(Calendar.DAY_OF_MONTH) + 1) == (data.get(Calendar.DAY_OF_MONTH)))
		{
			return context.getString(R.string.tomorrow);
		}
		if(now.get(Calendar.DAY_OF_MONTH) == (data.get(Calendar.DAY_OF_MONTH) + 1))
		{
			return context.getString(R.string.yesterday);
		}
		if(((now.get(Calendar.DAY_OF_MONTH) - data.get(Calendar.DAY_OF_MONTH)) < 7)
				&& (now.get(Calendar.DAY_OF_WEEK) > data.get(Calendar.DAY_OF_WEEK)))
		{
			return getWeek(data.get(Calendar.DAY_OF_WEEK));
		}
		return getDateFormat(context, R.string.date_format_month_day).format(data.getTime());
	}

	/**
	 * @param ts
	 *            - timestamp
	 * @return date in 'mm/dd hh:mm' format
	 */
	public static String getDate2(final Context context, final long ts)
	{
		if(isSameYear(ts))
		{
			return getDateFormat(context, R.string.date_format_month_day_time).format(new Date(ts));
		}
		return getDateFormat(context, R.string.date_format_year_month_day_time).format(new Date(ts));
	}

	private static SimpleDateFormat getDateFormat(final Context context, final int res)
	{
		final Integer key = Integer.valueOf(res);
		SimpleDateFormat simpleDateFormat = dateFormats.get(key);
		if(simpleDateFormat == null)
		{
			simpleDateFormat = new LocaleDateFormat(context, res);
			dateFormats.put(key, simpleDateFormat);
		}
		return simpleDateFormat;
	}

	public static long getDelta()
	{
		return delta;
	}

	public static int getMinutes(final long timeDiff)
	{
		return (int)(timeDiff / (1000 * 60));
	}

	private static String getWeek(final int week)
	{
		return weekdays[week];
	}

	public static boolean isSameDay(final Date d1, final Date d2)
	{
		assert d1 != null;
		assert d2 != null;

		synchronized(calendar)
		{
			calendar.setTime(d1);
			final int day = getCalendarDay();
			final int year = getCalendarYear();

			calendar.setTime(d2);

			return (day == getCalendarDay()) && (year == getCalendarYear());
		}
	}

	public static boolean isSameYear(final long time)
	{
		final GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date(time));
		final int timeYear = calendar.get(Calendar.YEAR);
		final int currentYear = DateUtils.getCurrentYear();

		return currentYear == timeYear;
	}

	public static void onChangedLocale()
	{
		customDateFormatter = new SimpleDateFormat();
	}

	public static Date parse(final String sdate, final String pattern, final Date def)
	{
		try
		{
			final SimpleDateFormat format = new SimpleDateFormat(pattern);
			return format.parse(sdate);
		}
		catch(final Throwable e)
		{
			Log.e(THIS_FILE, "parse()", e);
			return def;
		}
	}

	public static final void setDelta(final long d)
	{
		delta = d;
	}

	private DateUtils()
	{
		super();
	}
}
