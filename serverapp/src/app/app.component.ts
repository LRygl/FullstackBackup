import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, startWith } from 'rxjs/operators';
import { DataState } from './Enum/data-state.enum';
import { Status } from './Enum/status.enum';
import { AppState } from './Interface/app-state';
import { CustomResponse } from './Interface/custom-response';
import { Server } from './Interface/server';
import { NotificationService } from './Service/notification.service';
import { ServerService } from './Service/server.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  appState$: Observable<AppState<CustomResponse>>;

  readonly DataState = DataState;
  readonly Status = Status
  private filterSubject = new BehaviorSubject<string>('');
  private dataSubject = new BehaviorSubject<CustomResponse>(null);
  filterStatus$ = this.filterSubject.asObservable();
  private isLoading = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoading.asObservable();


  constructor(private serverService: ServerService, private notifier: NotificationService) {  }

  ngOnInit(): void{
    this.appState$ = this.serverService.servers$
    .pipe(
      map(response => {
        this.notifier.onDefault(response.message)
        this.dataSubject.next(response);
        return { dataState: DataState.LOADED_STATE, appData: {...response, data:{servers: response.data.servers.reverse()}} }
      }),
      startWith({ dataState: DataState.LOADING_STATE, appData: null }),
      catchError((error: string) => {
        this.notifier.onError(error);
        return of({ dataState: DataState.ERROR_STATE, error: error })
      })
    );
  }

  pingServer(ipAddress: string): void{
    this.filterSubject.next(ipAddress);
    this.appState$ = this.serverService.ping$(ipAddress)
    .pipe(
      map(response => {
        this.notifier.onDefault(response.message)
        const index = this.dataSubject.value.data.servers.findIndex(server => server.id === response.data.server.id);
        this.dataSubject.value.data.servers[index] = response.data.server;
        this.filterSubject.next('');
        return { dataState: DataState.LOADED_STATE, appData:  this.dataSubject.value  }
      }),
      startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
      catchError((error: string) => {
        this.notifier.onError(error);
        this.filterSubject.next('');
        return of({ dataState: DataState.ERROR_STATE, error: error })
      })
    );
  }

  saveServer(serverForm: NgForm): void{
    this.isLoading.next(true);
    this.appState$ = this.serverService.save$(serverForm.value as Server)
    .pipe(
      map(response => {
        this.notifier.onDefault(response.message);
        this.dataSubject.next(
          {...response, data: {servers: [response.data.server, ...this.dataSubject.value.data.servers]} }
        );
        document.getElementById('closeModal').click();
        this.isLoading.next(false);
        serverForm.resetForm( {status: this.Status.SERVER_DOWN} )
        return { dataState: DataState.LOADED_STATE, appData:  this.dataSubject.value  }
      }),
      startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
      catchError((error: string) => {
        this.notifier.onError(error);
        this.isLoading.next(false);
        return of({ dataState: DataState.ERROR_STATE, error: error })
      })
    );
  }

  deleteServer(server: Server): void{
    this.appState$ = this.serverService.delete$(server.id)
    .pipe(
      map(response => {
        this.notifier.onDefault(response.message);
         this.dataSubject.next(
            {...response, data: {servers: this.dataSubject.value.data.servers.filter(serverItem => serverItem.id !== server.id)}}
         );
        return { dataState: DataState.LOADED_STATE, appData:  this.dataSubject.value  }
      }),
      startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
      catchError((error: string) => {
        this.notifier.onError(error);
        return of({ dataState: DataState.ERROR_STATE, error: error })
      })
    );
  }

  printReport(): void{
    // window.print();
    let dataType = 'application/vnd.ms-exce.sheet.macroEnabled.12';
    let tableSelect = document.getElementById('servers');
    let tableHtml = tableSelect.outerHTML.replace(/ /g, '%20')
    let downloadLink = document.createElement('a');
    document.body.appendChild(downloadLink);
    downloadLink.href = 'data:' + dataType + ', ' + tableHtml;

    downloadLink.download = 'server-report.xls';
    downloadLink.click();
    document.body.removeChild(downloadLink);

  }


 /*  filterServers(status: Status): void {
    this.appState$ = this.serverService.filter$(status, this.dataSubject.value)
      .pipe(
        map(response => {
          return { dataState: DataState.LOADED_STATE, appData: response };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          return of({ dataState: DataState.ERROR_STATE, error });
        })
      );
  } */

}

