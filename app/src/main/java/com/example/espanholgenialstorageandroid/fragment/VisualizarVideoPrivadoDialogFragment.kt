package com.example.espanholgenialstorageandroid.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.espanholgenialstorageandroid.R

class VisualizarVideoPrivadoDialogFragment : DialogFragment()
{
    private var videoUrl: String? = null
    private var campoInformativo: String? = null
    private var nomeVideo: String? = null

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    companion object {
        private const val ARG_VIDEO_URL = "video_url"
        private const val ARG_CAMPO_TEXTO = "campo_texto"
        private const val ARG_NOME_VIDEO = "nome_video"

        fun newInstance(videoUrl: String, campoInformativo: String, nomeVideo: String): VisualizarVideoPrivadoDialogFragment {
            val fragment = VisualizarVideoPrivadoDialogFragment()
            val args = Bundle()
            args.putString(ARG_VIDEO_URL, videoUrl)
            args.putString(ARG_CAMPO_TEXTO, campoInformativo)
            args.putString(ARG_NOME_VIDEO, nomeVideo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_visualizar_video_privado, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNome = view.findViewById<TextView>(R.id.tvNomeVideo)
        val tvNomeFirebase = view.findViewById<TextView>(R.id.tvNomeVideoFirebase)
        playerView = view.findViewById(R.id.playerView)

        videoUrl = arguments?.getString(ARG_VIDEO_URL)
        campoInformativo = arguments?.getString(ARG_CAMPO_TEXTO)
        nomeVideo = arguments?.getString(ARG_NOME_VIDEO)

        tvNome.text = "Nome do vídeo:"
        tvNomeFirebase.text = nomeVideo ?: "Vídeo desconhecido"

        // Cria o player
        player = ExoPlayer.Builder(requireContext()).build()
        playerView.player = player

        // Configura o vídeo
        videoUrl?.let { url ->
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerView.player = null
        player?.release()
        player = null
    }
}