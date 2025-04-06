import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HomepageComponent } from './homepage.component';

@NgModule({
  declarations: [
    HomepageComponent
  ],
  imports: [
    CommonModule, 
    FormsModule 
  ],
  exports: [
    HomepageComponent 
  ]
})
export class HomepageModule { }
