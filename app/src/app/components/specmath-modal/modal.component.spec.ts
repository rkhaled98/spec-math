// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { ModalComponent } from './modal.component';

import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { BrowserModule, By } from '@angular/platform-browser';
import { MatStepperModule } from '@angular/material/stepper';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { queryElement } from '../../../shared/functions';
import { Component, Output, EventEmitter } from '@angular/core';
import { SpecNameInputOptions } from 'src/shared/interfaces';

describe('ModalComponent', () => {
  let fixture: ComponentFixture<ModalComponent>;
  let modal: ModalComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ModalComponent,
        SpecNameInputStubComponent
      ],
      imports: [
        MatStepperModule,
        MatButtonModule,
        MatDialogModule,
        MatIconModule,
        MatInputModule,
        MatTooltipModule,
        BrowserModule,
        MatDialogModule,
        BrowserAnimationsModule,
      ],
      providers: [
        {
          provide: MatDialogRef, useValue: { close: () => {} }
        },
      ]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(ModalComponent);
      modal = fixture.componentInstance;
    });
  }));

  const getSpecNameInputStubComponent = () =>
    fixture.debugElement.query(By.directive(SpecNameInputStubComponent))
      .injector.get(SpecNameInputStubComponent);

  it('creates the modal component', () => {
    expect(modal).toBeTruthy();
  });

  it('is opened', () => {
    expect(queryElement(fixture, '.modal-container')).toBeTruthy();
  });

  it('is closed when then cancel button is pressed', () => {
    const spy = spyOn(modal.dialogRef, 'close').and.callThrough();
    const cancelButton = queryElement(fixture, '#modal-cancel-button').nativeElement;

    cancelButton.click();
    expect(spy).toHaveBeenCalled();
  });

  it('disables the next button when the modal is first opened', () => {
    fixture.detectChanges();
    const nextButton = queryElement(fixture, '#modal-button-next').nativeElement;

    expect(nextButton.disabled).toBeTruthy();
  });

  it('enables the next button when newFileName is valid', () => {
    fixture.detectChanges();
    const nextButton = queryElement(fixture, '#modal-button-next').nativeElement;

    getSpecNameInputStubComponent().emitOptions();
    fixture.detectChanges();
    expect(nextButton.disabled).toBeFalsy();
  });

  it('moves onto the next step when next button is clicked', () => {
    fixture.detectChanges();
    const nextButton = queryElement(fixture, '#modal-button-next').nativeElement;

    getSpecNameInputStubComponent().emitOptions();
    fixture.detectChanges();
    nextButton.click();
    fixture.detectChanges();
    expect(modal.currentStep).toEqual(1);
  });
});

/**
 * Stub component for the SpecNameInputComponent
 */
@Component({
  selector: 'app-spec-name-input',
  template: '<div>spec-name-input stub</div>'
})
export class SpecNameInputStubComponent {
  @Output() options = new EventEmitter<SpecNameInputOptions>();

  emitOptions() {
    this.options.emit({ newFileName: 'new_spec', valid: true });
  }
}
