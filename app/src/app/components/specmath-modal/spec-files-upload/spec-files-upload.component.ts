// Copyright 2020 Google LLC

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this spec except in compliance with the License.
// You may obtain a copy of the License at

//     https://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { SpecFilesUploadOptions } from '../../../../shared/interfaces';

@Component({
  selector: 'app-spec-files-upload',
  templateUrl: './spec-files-upload.component.html',
  styleUrls: ['./spec-files-upload.component.scss']
})
export class SpecFilesUploadComponent implements OnInit  {
  specFilesNum: number;
  fileUploadError: boolean;
  specFilesUploadOptions?: SpecFilesUploadOptions = {
    specFiles: [],
    valid: false
  };

  @Output() options: EventEmitter<SpecFilesUploadOptions> = new EventEmitter();

  constructor() {

  }

  handleSpecFileInput(files: FileList) {
    const emptyFileSpots =
      this.specFilesNum - this.specFilesUploadOptions.specFiles.length;

    if (files.length > emptyFileSpots) {
      this.fileUploadError = true;
      return;
    } else {
      this.fileUploadError = false;
    }

    if (files.length <= emptyFileSpots) {
      this.specFilesUploadOptions.specFiles.push(...Array.from(files));
    }

    if (this.specFilesUploadOptions.specFiles.length === this.specFilesNum) {
      this.specFilesUploadOptions.valid = true;
      this.options.emit(this.specFilesUploadOptions);
    }
  }

  removeSpecFile(index: number) {
    this.specFilesUploadOptions.specFiles.splice(index, 1);
  }

  ngOnInit() {
    this.specFilesNum = 3;
    this.fileUploadError = false;
  }
}
