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

import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { SpecNameInputOptions, DefaultsFileUploadOptions, SpecFilesUploadOptions } from 'src/shared/interfaces';

enum steps {
  specNameInput = 0,
  defaultsFileUpload = 1,
  specFilesUpload = 2,
  confirmOperation = 3,
}

type StepOptions = {
  [key in steps]: {
    toolTipText: string,
    nextStep?: number,
    previousStep?: number
  };
};

const stepOptions: StepOptions = {
  [steps.specNameInput]: {
    toolTipText: 'You must name your new spec',
    nextStep: steps.defaultsFileUpload,
  },
  [steps.defaultsFileUpload]: {
    toolTipText: '',
    nextStep: steps.specFilesUpload,
    previousStep: steps.specNameInput
  },
  [steps.specFilesUpload]: {
    toolTipText: 'You must upload a set of spec files',
    nextStep: steps.confirmOperation,
    previousStep: steps.defaultsFileUpload
  },
  [steps.confirmOperation]: {
    toolTipText: '',
    previousStep: steps.specFilesUpload
  }
};

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss']
})
export class ModalComponent {
  FIRST_STEP = steps.specNameInput;
  LAST_STEP = steps.confirmOperation;
  currentStep: steps = steps.specNameInput;
  specNameInputOptions: SpecNameInputOptions = {
    newFileName: '',
    valid: false
  };
  defaultsFileUploadOptions: DefaultsFileUploadOptions = {
    defaultsFile: null
  };
  specFilesUploadOptions: SpecFilesUploadOptions = {
    specFiles: [],
    valid: false,
  };

  constructor(readonly dialogRef: MatDialogRef<ModalComponent>) {
    dialogRef.disableClose = true;
  }

  nextStep(stepper: MatStepper): void {
    this.currentStep = stepOptions[this.currentStep].nextStep;
    stepper.selectedIndex = this.currentStep;
  }

  previousStep(stepper: MatStepper): void {
    this.currentStep = stepOptions[this.currentStep].previousStep;
    stepper.selectedIndex = this.currentStep;
  }

  get newFileName(): string {
    return this.specNameInputOptions?.newFileName || '';
  }

  get nextButtonTooltipText(): string {
    return stepOptions[this.currentStep].toolTipText;
  }

  get nextButtonEnabled(): boolean {
    switch (this.currentStep) {
      case steps.specNameInput:
        return this.specNameInputOptions?.valid;
      case steps.specFilesUpload:
        return this.specFilesUploadOptions?.valid;
      default:
        return true;
    }
  }

  handleSpecNameInputOptions(specNameInputOptions: SpecNameInputOptions) {
    this.specNameInputOptions = specNameInputOptions;
  }

  handleDefaultsFileUploadOptions(defaultsFileUploadOptions: DefaultsFileUploadOptions) {
    this.defaultsFileUploadOptions = defaultsFileUploadOptions;
  }

  handleSpecFilesUploadOptions(specFilesUploadOptions: SpecFilesUploadOptions) {
    this.specFilesUploadOptions = specFilesUploadOptions;
  }
}
