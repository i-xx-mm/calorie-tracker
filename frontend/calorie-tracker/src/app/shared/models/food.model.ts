/**
 * Food template entity, matches backend Food MongoDB document
 * Used for autocomplete search dropdown
 */
export interface Food {
  /** MongoDB document id, optional for create payload */
  id?: string;
  /** Normalized lowercase food name */
  name: string;
  /** Calories per serving for this food template */
  calorie: number;
}

/**
 * Single food entry item stored inside daily FoodLog document
 * Represents one consumed food record on a specific day
 */
export interface FoodItem {
  /** Unique identifier for log item */
  id?: string;
  /** Display food name shown in food log list */
  name: string;
  /** Calories consumed for this entry */
  calorie: number;
  /** User personal note for this specific log entry */
  note?: string;
}

/**
 * Daily food log record for one user on a single date
 * Contains list of eaten food items and aggregated calorie sum
 */
export interface FoodLog {
  /** MongoDB document id */
  id?: string;
  /** Owner username for this food log */
  username: string;
  /** Target log date in ISO string format */
  date: string;
  /** Array of food items consumed on this day */
  foods: FoodItem[];
  /** Pre-computed total calories sum for the day */
  totalCalories: number;
}

/**
 * Calorie consumption calculation block for dashboard view
 * Holds consumed, suggested target, remaining calories, and completion percentage
 */
export interface CalorieTracking {
  /** Total calories already consumed today */
  consumed: number;
  /** Calculated daily suggested calorie goal */
  suggestedDaily: number;
  /** Remaining allowed calories for today */
  remaining: number;
  /** Percentage of daily goal already consumed (0-100) */
  percentage: number;
}

/**
 * Aggregated dashboard view data
 * Combines calorie tracking, BMI metadata, and food log count
 */
export interface DashboardData {
  /** Target logged-in user name */
  username: string;
  /** Dashboard report target date */
  date: string;
  /** Daily calorie tracking metrics */
  calorieTracking: CalorieTracking;
  /** Calculated user BMI result */
  bmi: {
    value: number;
    category: string;
    status: string;
  };
  /** Count of food entries logged on this date */
  foodsLogged: number;
}

/**
 * Single daily data point inside monthly statistics report
 */
export interface MonthlyStat {
  /** Date string for this statistics entry */
  date: string;
  /** Total consumed calories on this day */
  totalCalories: number;
}

/**
 * Response payload from monthly-stats history API endpoint.
 * Provides daily time-series data plus high-level summary aggregations.
 */
export interface MonthlyStatsResponse {
  /** Requested user username */
  username: string;
  /** Human-readable time range of report, example: "2026-05-01 to 2026-08-01" */
  period: string;
  /** Array of daily calorie records; empty array if no log history exists */
  dailyData: MonthlyStat[];
  /** Sggregated statistics summary */
  summary: {
    /** Average daily calorie consumption across days that contain food logs */
    averageDailyConsumption: number;
    /** Date string of day with maximum total calorie intake */
    highestDay: string;
    /** Total calories consumed on highest-intake day */
    highestDayCalories: number;
    /** Date string of day with minimum total calorie intake */
    lowestDay: string;
    /** Total calories consumed on lowest-intake day; 0 if no valid log entries */
    lowestDayCalories: number;
    /** Count of days that have at least one food log entry */
    daysWithLogs: number;
    /** Sum total calories consumed within the requested reporting period */
    totalLogged: number;
  };
}
