import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  switchMap,
  takeUntil,
} from 'rxjs/operators';
import { Food, FoodItem, FoodLog } from '../../shared/models/food.model';
import { FoodService } from '../../shared/services/food.service';
import { NotificationService } from '../../shared/services/notification.service';

/**
 * FoodLog Component
 * Handles daily food logging, food item search, add/edit/delete food entries
 */
@Component({
  selector: 'app-food-log',
  templateUrl: './food-log.component.html',
  styleUrls: ['./food-log.component.css'],
})
export class FoodLogComponent implements OnInit, OnDestroy {
  foodLogForm!: FormGroup;
  currentFoodLog: FoodLog | null = null;
  loading = true;
  searching = false;
  submitting = false;
  editing = false;
  editingIndex: number | null = null;

  searchResults: Food[] = [];
  selectedFood: Food | null = null;
  showSearchResults = false;

  // Modal states
  showEditModal = false;
  editingFood: FoodItem | null = null;
  showDeleteModal = false;
  deletingFood: FoodItem | null = null;
  deletingIndex: number | null = null;

  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private foodService: FoodService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadTodayFoodLog();

    // Debounced food‑search stream
    // - debounceTime(300): wait for user to stop typing before sending request
    // - distinctUntilChanged(): skip emitting if search term unchanged
    // - switchMap: cancel previous in‑flight http request on new input, avoid stale response
    //   empty term returns empty array to clear search result
    // - takeUntil(this.destroy$): auto unsubscribe on component destroy to prevent memory leak
    // On next: update result list, toggle dropdown visibility, clear loading flag
    // On error: clear loading and show error notification
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((searchTerm) => {
          if (!searchTerm) {
            return [];
          }
          return this.foodService.searchFoods(searchTerm);
        }),
        takeUntil(this.destroy$),
      )
      .subscribe({
        next: (response) => {
          this.searchResults = response;
          this.showSearchResults = this.searchResults.length > 0;
          this.searching = false;
        },
        error: () => {
          this.searching = false;
          this.notificationService.error('Search failed');
        },
      });
  }

  ngOnDestroy(): void {
    this.loading = false;
    this.destroy$.next();
    this.destroy$.complete();
  }

  initializeForm(): void {
    this.foodLogForm = this.formBuilder.group({
      foodSearch: ['', Validators.required],
      calorie: [
        null,
        [
          Validators.required,
          Validators.min(1),
          Validators.max(10000),
          this.positiveNumberValidator.bind(this),
        ],
      ],
      note: [''],
    });
  }

  // Custom validator to prevent negative numbers
  positiveNumberValidator(control: any): { [key: string]: any } | null {
    if (!control.value) return null;
    const value = parseFloat(control.value);
    if (isNaN(value) || value < 0) {
      return { negativeNumber: true };
    }
    return null;
  }

  loadTodayFoodLog(): void {
    this.loading = true;
    const today = this.getTodayDateEST();
    this.foodService
      .getFoodLogByDate(today)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (foodLog) => {
          this.currentFoodLog = foodLog;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        },
      });
  }

  /**
   * Handles input change event for food search text input
   * Emits trimmed search term to searchSubject for debounced backend query
   * Immediately hides dropdown and clears result array when input becomes empty,
   * to avoid waiting for debounce delay for UI update
   * Sets searching loading flag when non‑empty search term is provided.
   *
   * @param event input change event from text input element
   */
  onFoodSearch(event: any): void {
    const searchTerm = event.target.value.trim();
    this.searchSubject.next(searchTerm);

    if (searchTerm.length < 1) {
      this.showSearchResults = false;
      this.searchResults = [];
      return;
    }

    this.searching = true;
  }

  clearFoodSearch(): void {
    this.foodLogForm.patchValue({
      foodSearch: '',
    });
    this.showSearchResults = false;
    this.searchResults = [];
    this.selectedFood = null;
    this.searching = false;
  }

  selectFood(food: Food): void {
    this.selectedFood = food;
    this.foodLogForm.patchValue({
      foodSearch: food.name,
      calorie: food.calorie,
    });
    this.showSearchResults = false;
  }

  onSubmit(): void {
    if (this.foodLogForm.invalid) {
      this.notificationService.error(
        'Please fill in all required fields correctly',
      );
      return;
    }

    this.submitting = true;
    const note = this.foodLogForm.get('note')?.value || '';
    const finalCalorie = Number(this.foodLogForm.get('calorie')?.value);
    const finalFoodName = this.foodLogForm.get('foodSearch')?.value;

    if (isNaN(finalCalorie)) {
      this.notificationService.error('Calorie must be valid number');
      this.submitting = false;
      return;
    }

    if (this.editing && this.editingIndex !== null && this.currentFoodLog) {
      // Update existing entry
      this.foodService
        .updateFoodEntry(
          this.currentFoodLog.id || '',
          this.editingIndex,
          finalFoodName,
          finalCalorie,
          note,
        )
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (updatedLog) => {
            this.currentFoodLog = updatedLog;
            this.resetForm();
            this.notificationService.success('Food entry updated');
            this.submitting = false;
          },
          error: () => {
            this.submitting = false;
            this.notificationService.error('Failed to update entry');
          },
        });
    } else {
      // Add new entry
      this.foodService
        .addFoodEntry(finalFoodName, finalCalorie, note, this.getTodayDateEST())
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (foodLog) => {
            this.currentFoodLog = foodLog;
            this.resetForm();
            this.notificationService.success('Food entry added');
            this.submitting = false;
          },
          error: () => {
            this.submitting = false;
            this.notificationService.error('Failed to add entry');
          },
        });
    }
  }

  editEntry(index: number): void {
    if (!this.currentFoodLog) return;
    const item = this.currentFoodLog.foods[index];
    this.editing = true;
    this.editingIndex = index;
    this.editingFood = item;
    this.showEditModal = true;
  }

  onEditSave(updatedFood: FoodItem): void {
    if (!this.currentFoodLog || this.editingIndex === null) return;

    const originalFood = this.currentFoodLog.foods[this.editingIndex];
    const nameChanged =
      updatedFood.name.toLowerCase() !== originalFood.name.toLowerCase();
    const calorieChanged = updatedFood.calorie !== originalFood.calorie;

    if (nameChanged || calorieChanged) {
      // first perform delete then add
      // switchMap to chain sequential HTTP calls
      this.foodService
        .deleteFoodEntry(this.currentFoodLog.id || '', this.editingIndex)
        .pipe(
          switchMap(() =>
            this.foodService.addFoodEntry(
              updatedFood.name,
              updatedFood.calorie,
              updatedFood.note,
              this.getTodayDateEST(),
            ),
          ),
          takeUntil(this.destroy$),
        )
        .subscribe({
          next: (updatedLog) => {
            this.currentFoodLog = updatedLog;
            this.showEditModal = false;
            this.editingFood = null;
            this.editingIndex = null;
            this.notificationService.success('Food entry updated');
          },
          error: () => {
            this.notificationService.error('Failed to update entry');
          },
        });
    } else {
      // only note changed
      this.foodService
        .updateFoodEntry(
          this.currentFoodLog.id || '',
          this.editingIndex,
          updatedFood.name,
          updatedFood.calorie,
          updatedFood.note,
        )
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (updatedLog) => {
            this.currentFoodLog = updatedLog;
            this.showEditModal = false;
            this.editingFood = null;
            this.editingIndex = null;
            this.notificationService.success('Food entry updated');
          },
          error: () => {
            this.notificationService.error('Failed to update entry');
          },
        });
    }
  }

  onEditCancel(): void {
    this.showEditModal = false;
    this.editingFood = null;
    this.editingIndex = null;
  }

  deleteEntry(index: number): void {
    if (!this.currentFoodLog) return;
    const item = this.currentFoodLog.foods[index];
    this.deletingIndex = index;
    this.deletingFood = item;
    this.showDeleteModal = true;
  }

  onDeleteConfirm(): void {
    if (!this.currentFoodLog || this.deletingIndex === null) return;

    this.foodService
      .deleteFoodEntry(this.currentFoodLog.id || '', this.deletingIndex)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedLog) => {
          this.currentFoodLog = updatedLog;
          this.showDeleteModal = false;
          this.deletingFood = null;
          this.deletingIndex = null;
          this.notificationService.success('Food entry deleted');
        },
        error: () => {
          this.notificationService.error('Failed to delete entry');
        },
      });
  }

  onDeleteCancel(): void {
    this.showDeleteModal = false;
    this.deletingFood = null;
    this.deletingIndex = null;
  }

  resetForm(): void {
    this.foodLogForm.reset();
    this.selectedFood = null;
    this.editing = false;
    this.editingIndex = null;
    this.showSearchResults = false;
  }

  cancelEdit(): void {
    this.resetForm();
  }

  getTotalCalories(): number {
    if (
      !this.currentFoodLog ||
      !this.currentFoodLog.foods ||
      !Array.isArray(this.currentFoodLog.foods)
    ) {
      return 0;
    }
    return this.currentFoodLog.foods.reduce(
      (sum, food) => sum + food.calorie,
      0,
    );
  }

  get f() {
    return this.foodLogForm.controls;
  }

  getTodayDateFormatted(): string {
    return new Date().toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  /**
   * Get today date string (YYYY‑MM‑DD) in America/New_York timezone
   */
  private getTodayDateEST(): string {
    const date = new Date();
    const nyTime = new Intl.DateTimeFormat('en-US', {
      timeZone: 'America/New_York',
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    }).formatToParts(date);

    const year = nyTime.find((p) => p.type === 'year')!.value;
    const month = nyTime.find((p) => p.type === 'month')!.value;
    const day = nyTime.find((p) => p.type === 'day')!.value;

    return `${year}-${month}-${day}`;
  }

  @HostListener('document:click')
  onDocumentClick(): void {
    this.showSearchResults = false;
  }
}
