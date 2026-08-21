export interface Food {
  id?: string;
  name: string;
  calorie: number;
  note?: string;
}

export interface FoodItem {
  id?: string;
  name: string;
  calorie: number;
  note?: string;
}

export interface FoodLog {
  id?: string;
  username: string;
  date: string;
  foods: FoodItem[];
  totalCalories: number;
}

export interface CalorieTracking {
  consumed: number;
  suggestedDaily: number;
  remaining: number;
  percentage: number;
}

export interface DashboardData {
  username: string;
  date: string;
  calorieTracking: CalorieTracking;
  bmi: {
    value: number;
    category: string;
    status: string;
  };
  foodsLogged: number;
}

export interface MonthlyStat {
  date: string;
  totalCalories: number;
}

export interface MonthlyStatsResponse {
  username: string;
  period: string;
  dailyData: MonthlyStat[] | null;
  summary: {
    averageDailyConsumption: number;
    highestDay: string;
    highestDayCalories: number;
    lowestDay: string;
    lowestDayCalories: number;
    daysWithLogs: number;
    totalLogged: number;
  };
}