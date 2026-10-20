package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.remote.CampaignUpdateRequest

class CampaignRepository {

    suspend fun getCampaigns(): List<CampaignResponse> {
        return ApiClient.apiService.getCampaigns()
    }

    suspend fun updateCampaign(
        token: String,
        campaignId: Int,
        request: CampaignUpdateRequest
    ): CampaignResponse {
        return ApiClient.apiService.updateCampaign("Bearer $token", campaignId, request)
    }
}
