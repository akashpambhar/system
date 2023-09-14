import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SchoolPerformanceClassnameSubjectnameComponent } from './school-performance-classname-subjectname.component';

describe('SchoolPerformanceClassnameSubjectnameComponent', () => {
  let component: SchoolPerformanceClassnameSubjectnameComponent;
  let fixture: ComponentFixture<SchoolPerformanceClassnameSubjectnameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SchoolPerformanceClassnameSubjectnameComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SchoolPerformanceClassnameSubjectnameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
