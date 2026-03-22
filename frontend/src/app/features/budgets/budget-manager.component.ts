import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Budget, Category } from '../../core/models/app.models';

@Component({
    selector: 'app-budget-manager',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './budget-manager.component.html',
    styleUrls: ['./budget-manager.component.css']
})
export class BudgetManagerComponent implements OnInit {
    budgets: Budget[] = [];
    categories: Category[] = [];
    budgetForm: FormGroup;

    private apiService = inject(ApiService);
    private fb = inject(FormBuilder);

    constructor() {
        this.budgetForm = this.fb.group({
            categoryId: ['', Validators.required],
            amount: ['', [Validators.required, Validators.min(1)]],
            warningThreshold: [80, [Validators.required, Validators.min(1), Validators.max(100)]]
        });
    }

    ngOnInit() {
        this.loadBudgets();
        this.loadCategories();
    }

    loadBudgets() {
        this.apiService.getBudgets().subscribe({
            next: (res) => this.budgets = res
        });
    }

    loadCategories() {
        this.apiService.getCategories().subscribe({
            next: (res) => this.categories = res
        });
    }

    onSubmit() {
        if (this.budgetForm.valid) {
            this.apiService.saveBudget(this.budgetForm.value).subscribe({
                next: () => {
                    this.loadBudgets();
                    this.budgetForm.reset({ warningThreshold: 80 });
                }
            });
        }
    }
}
