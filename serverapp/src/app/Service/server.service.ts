import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Status } from '../Enum/status.enum';
import { CustomResponse } from '../Interface/custom-response';
import { Server } from '../Interface/server';

@Injectable({ providedIn: 'root' })
export class ServerService {

  constructor(private http: HttpClient) { }

  private readonly apiUrl = 'http://localhost:8081'

  // getServers(): Observable<CustomResponse> {
  // return this.http.get<CustomResponse>('http://localhost:8081/server/list');
  // }

  //$ denotes observable
  servers$ = <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${this.apiUrl}/server/list`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  save$ = (server: Server) => <Observable<CustomResponse>>
    this.http.post<CustomResponse>(`${this.apiUrl}/server/save`, server)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  ping$ = (ipAddress: string) => <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${this.apiUrl}/server/ping/${ipAddress}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  filter$ = (status: Status, response: CustomResponse) => <Observable<CustomResponse>>
    new Observable<CustomResponse>(
      subscriber => {
        console.log(response);
        subscriber.next(
          status === Status.ALL ? { ...response, message: `Servers filtered by ${status} status` } :
          {
            ...response,
            message: response.data.servers.filter(server => server.status === status).length > 0 ? `Servers filtered by 
            ${status === Status.SERVER_UP ? 'SERVER UP' : 'SERVER DOWN'} status` : `No servers of ${status} found`,
            data: { servers: response.data.servers.filter(server => server.status === status)}
          }
        );
      subscriber.complete()
      }
    )
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  delete$ = (serverId: number) => <Observable<CustomResponse>>
    this.http.delete<CustomResponse>(`${this.apiUrl}/server/delete/${serverId}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );


  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log(error)
    return throwError(`An Error Occured - Error Code: ${error.status}`)
  }
}
