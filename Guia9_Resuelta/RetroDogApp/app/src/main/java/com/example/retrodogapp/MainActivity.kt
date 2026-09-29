package com.example.retrodogapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrodogapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMainBinding
    private val dogAdapter = DogAdapter()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingSearch: Runnable? = null
    private var activeCall: Call<DogsResponse>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, maxOf(bars.bottom, ime.bottom))
            insets
        }
        binding.listDogs.layoutManager = LinearLayoutManager(this)
        binding.listDogs.adapter = dogAdapter
        binding.searchDogs.setOnQueryTextListener(this)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        queueSearch(newText, false)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        queueSearch(query, true)
        binding.searchDogs.clearFocus()
        return true
    }

    private fun queueSearch(query: String?, immediate: Boolean) {
        pendingSearch?.let(handler::removeCallbacks)
        pendingSearch = null
        activeCall?.cancel()
        activeCall = null
        dogAdapter.submitList(emptyList())
        val breed = query.orEmpty().trim().lowercase(Locale.ROOT)
        if (breed.isEmpty()) {
            showMessage(R.string.search_instructions)
            return
        }
        binding.progress.isVisible = true
        binding.statusMessage.isVisible = false
        if (immediate) {
            searchByName(breed)
        } else {
            // Esperar brevemente evita una petición por cada tecla.
            pendingSearch = Runnable { searchByName(breed) }.also { handler.postDelayed(it, 400) }
        }
    }

    private fun searchByName(breed: String) {
        val request = RetrofitClient.instance.getDogsByBreed(breed)
        activeCall = request
        request.enqueue(object : Callback<DogsResponse> {
            override fun onResponse(call: Call<DogsResponse>, response: Response<DogsResponse>) {
                if (call !== activeCall) return
                activeCall = null
                val body = response.body()
                if (response.isSuccessful && body?.status == "success") {
                    val images = body.images.orEmpty().filterNotNull().filter { it.isNotBlank() }
                    dogAdapter.submitList(images)
                    if (images.isEmpty()) showMessage(R.string.no_results)
                    else {
                        binding.progress.isVisible = false
                        binding.statusMessage.isVisible = false
                        binding.listDogs.scrollToPosition(0)
                    }
                } else {
                    showMessage(if (response.code() == 404) R.string.no_results else R.string.server_error)
                }
            }

            override fun onFailure(call: Call<DogsResponse>, error: Throwable) {
                if (call !== activeCall || call.isCanceled) return
                activeCall = null
                showMessage(R.string.connection_error)
            }
        })
    }

    private fun showMessage(message: Int) {
        binding.progress.isVisible = false
        binding.statusMessage.setText(message)
        binding.statusMessage.isVisible = true
    }

    override fun onDestroy() {
        pendingSearch?.let(handler::removeCallbacks)
        activeCall?.cancel()
        activeCall = null
        binding.listDogs.adapter = null
        super.onDestroy()
    }
}
