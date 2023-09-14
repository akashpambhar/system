import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentPerformanceClassnameComponent } from './student-performance-classname.component';

describe('StudentPerformanceClassnameComponent', () => {
  let component: StudentPerformanceClassnameComponent;
  let fixture: ComponentFixture<StudentPerformanceClassnameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StudentPerformanceClassnameComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentPerformanceClassnameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
