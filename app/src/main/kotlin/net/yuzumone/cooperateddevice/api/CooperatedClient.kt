/*
 * Copyright (C) 2017 yuzumone
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.yuzumone.cooperateddevice.api

import net.yuzumone.cooperateddevice.Model.Result
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import java.util.*

class CooperatedClient(client: OkHttpClient) {

    private val service: CooperatedService

    init {
        val uri = "http://192.168.42.254:4567"
        val retrofit = Retrofit.Builder().client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(uri)
                .build()
        service = retrofit.create(CooperatedService::class.java)
    }

    fun postMac(body: HashMap<String, String>): Observable<Result> {
        return service.postMac("application/json", "application/json", body)
    }
}