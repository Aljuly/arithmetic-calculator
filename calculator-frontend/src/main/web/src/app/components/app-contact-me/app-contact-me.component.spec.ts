import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppContactMeComponent } from './app-contact-me.component';

describe('AppContactMeComponent', () => {
  let component: AppContactMeComponent;
  let fixture: ComponentFixture<AppContactMeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AppContactMeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppContactMeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
