import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FoodItem } from '../../models/food.model';

@Component({
  selector: 'app-edit-food-modal',
  templateUrl: './edit-food-modal.component.html',
  styleUrls: ['./edit-food-modal.component.css']
})
export class EditFoodModalComponent {
  @Input() isOpen = false;
  @Input() foodItem: FoodItem | null = null;
  @Output() save = new EventEmitter<FoodItem>();
  @Output() close = new EventEmitter<void>();

  editForm!: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    this.initializeForm();
  }

  ngOnChanges(): void {
    if (this.isOpen && this.foodItem) {
      this.editForm.patchValue({
        name: this.foodItem.name,
        calorie: this.foodItem.calorie,
        note: this.foodItem.note || ''
      });
    }
  }

  private initializeForm(): void {
    this.editForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      calorie: ['', [Validators.required, Validators.min(1), Validators.max(10000)]],
      note: ['']
    });
  }

  onCancel(): void {
    this.close.emit();
  }

  onSave(): void {
    if (this.editForm.invalid || !this.foodItem) {
      return;
    }
    
    const updatedFood: FoodItem = {
      id: this.foodItem.id,
      name: this.editForm.get('name')?.value,
      calorie: this.editForm.get('calorie')?.value,
      note: this.editForm.get('note')?.value || ''
    };

    this.save.emit(updatedFood);
  }

  get f() {
    return this.editForm.controls;
  }
}