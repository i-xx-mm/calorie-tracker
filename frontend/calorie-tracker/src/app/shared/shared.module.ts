import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { HeaderComponent } from './components/header/header.component';
import { NotificationContainerComponent } from './components/notification-container/notification-container.component';
import { EditFoodModalComponent } from './components/edit-food-modal/edit-food-modal.component';
import { DeleteConfirmModalComponent } from './components/delete-confirm-modal/delete-confirm-modal.component';
import { CapitalizePipe } from './pipes/capitalize.pipe';

@NgModule({
  declarations: [
    HeaderComponent,
    NotificationContainerComponent,
    EditFoodModalComponent,
    DeleteConfirmModalComponent,
    CapitalizePipe
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    HeaderComponent,
    NotificationContainerComponent,
    EditFoodModalComponent,
    DeleteConfirmModalComponent,
    CapitalizePipe
  ]
})
export class SharedModule { }