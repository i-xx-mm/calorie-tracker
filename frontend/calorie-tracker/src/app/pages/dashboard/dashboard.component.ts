import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FoodService } from '../../shared/services/food.service';
import { UserService } from '../../shared/services/user.service';
import { NotificationService } from '../../shared/services/notification.service';
import { DashboardData, MonthlyStatsResponse } from '../../shared/models/food.model';
import { User } from '../../shared/models/user.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  dashboardData: DashboardData | null = null;
  monthlyStats: MonthlyStatsResponse | null = null;
  currentUser: User | null = null;
  loading = true;
  selectedMonth = new Date().toISOString().split('T')[0].substring(0, 7);

  // Chart dimensions
  private chartMarginLeft = 50;
  private chartMarginRight = 20;
  private chartMarginTop = 20;
  private chartMarginBottom = 30;
  private chartWidth = 900;
  private chartHeight = 240;

  constructor(
    private router: Router,
    private foodService: FoodService,
    private userService: UserService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.loadDashboardData();
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
      }
    });
  }

  loadDashboardData(): void {
    this.loading = true;
    this.foodService.getTodayDashboard().subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.loading = false;
      },
      error: (error) => {
        this.notificationService.error('Failed to load dashboard data');
        this.loading = false;
      }
    });

    this.foodService.getMonthlyStats(1).subscribe({
      next: (stats) => {
        if (!Array.isArray(stats.dailyData)) {
          stats.dailyData = [];
        }
        this.monthlyStats = stats;
      }
    });
  }

  getBMIStatus(bmi: number): string {
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }

  getBMIStatusClass(status: string): string {
    switch (status) {
      case 'Underweight':
        return 'status-blue';
      case 'Normal':
        return 'status-green';
      case 'Overweight':
        return 'status-orange';
      case 'Obese':
        return 'status-red';
      default:
        return '';
    }
  }

  getCaloriePercentageClass(percentage: number): string {
    if (!isFinite(percentage)) return '';
    if (percentage < 50) return 'progress-low';
    if (percentage < 100) return 'progress-normal';
    if (percentage < 150) return 'progress-high';
    return 'progress-exceed';
  }

  goToFoodLog(): void {
    this.router.navigate(['/food-log']);
  }

  getDateDay(dateString: string): number {
    return new Date(dateString).getDate();
  }

  // Line Chart Methods
  getChartWidth(): number {
    return this.chartWidth;
  }

  getCurrentUserDailyGoal(): number {
    if (this.currentUser?.goalCalories) {
      return this.currentUser.goalCalories;
    }

    //calculate if goalCalories was not calculated in backend
    //and if metrics available
    if (this.currentUser?.currentWeight && this.currentUser?.height &&
        this.currentUser?.age && this.currentUser?.gender) {
        const bmr = this.calculateBMR(
            this.currentUser.currentWeight,
            this.currentUser.height,
            this.currentUser.age,
            this.currentUser.gender
        );
        return Math.round(bmr * 1.55);
    }

    // default
    return 2000;
  }

  calculateBMR(weight: number, height: number, age: number, gender: string): number {
    // Mifflin-St Jeor formula
    if (gender === 'male') {
      return 10 * weight + 6.25 * height - 5 * age + 5;
    } else {
      return 10 * weight + 6.25 * height - 5 * age - 161;
    }
  }

  getYAxisLabels(): number[] {
    if (!this.monthlyStats) return [];
    
    const maxCalories = Math.max(
      this.monthlyStats.summary.highestDayCalories,
      this.getCurrentUserDailyGoal() + 200
    );
    
    // Generate 5 labels
    const step = Math.ceil(maxCalories / 5 / 100) * 100;
    const labels = [];
    for (let i = 0; i <= 4; i++) {
      labels.push(i * step);
    }
    return labels;
  }

  getMaxCalories(): number {
    if (!this.monthlyStats) return 2500;
    
    return Math.max(
      this.monthlyStats.summary.highestDayCalories,
      this.getCurrentUserDailyGoal() + 200
    );
  }

  getYPosition(calories: number): number {
    const maxCalories = this.getMaxCalories();
    const percentage = calories / maxCalories;
    const y = this.chartMarginTop + (this.chartHeight - (percentage * this.chartHeight));
    return y;
  }

  getXPosition(index: number): number {
    if (!this.monthlyStats || !this.monthlyStats.dailyData || this.monthlyStats.dailyData.length === 0) return this.chartMarginLeft;

    const dataCount = this.monthlyStats.dailyData.length;
    const availableWidth = this.chartWidth - this.chartMarginLeft - this.chartMarginRight;
    const spacing = availableWidth / (dataCount - 1 || 1);

    return this.chartMarginLeft + (index * spacing);
  }

  getLinePoints(): string {
    if (!this.monthlyStats || !this.monthlyStats.dailyData || this.monthlyStats.dailyData.length === 0) return '';

    return this.monthlyStats.dailyData
      .map((day, index) => {
        const x = this.getXPosition(index);
        const y = this.getYPosition(day.totalCalories);
        return `${x},${y}`;
      })
      .join(' ');
  }

  formatDateLabel(dateString: string): string {
    const [year,month,day] = dateString.split('-');
    return `${month}/${day}`;
  }

  getRingDashArray(percentage: number | undefined): string {
    const p = percentage ?? 0;
    const capped = Math.min(p, 100);
    return `${(capped / 100 * 340)} 340`;
  }
}