import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, startWith, map } from 'rxjs/operators';
import { DataState } from './Enum/data-state.enum';
import { AppState } from './Interface/app-state';
import { CustomResponse } from './Interface/custom-response';
import { ServerService } from './Service/server.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  appState$: Observable<AppState<CustomResponse>> | undefined;
  readonly DataState = DataState;

  constructor(private serverService: ServerService) { }

  ngOnInit(): void {
    this.appState$ = this.serverService.servers$
      .pipe(
        map(response => {
          return { dataState: DataState.LOADED_STATE}
        }),
        startWith({ dataState: DataState.LOADING_STATE }),
        catchError((error: string) => {
          return of({ dataState: DataState.ERROR_STATE, error });
        })
      );
  }

}

