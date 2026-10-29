package com.example.recipecataloglab.data

class PostsRepository(private val api: PostsApi = RetrofitClient.api) {

    suspend fun getPosts(): Result<List<Post>> = runCatching {
        api.getPosts()
    }

    suspend fun getPost(id: Int): Result<Post> = runCatching {
        api.getPost(id)
    }
}