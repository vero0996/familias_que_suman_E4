package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.CampaignResponse

class CampaignRepository {

    suspend fun getCampaigns(): List<CampaignResponse> {
        return ApiClient.apiService.getCampaigns()
    }
}