package com.example.espanholgenialstorageandroid.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.espanholgenialstorageandroid.R

class VisualizarAudioPublicoDialogFragment: DialogFragment()
{
    private var mediaPlayer: MediaPlayer? = null
    private var audioUrl: String? = null
    private var campoInformativo: String? = null
    private var nomeAudio: String? = null

    companion object {
        private const val ARG_AUDIO_URL = "audio_url"
        private const val ARG_CAMPO_TEXTO = "campo_texto"
        private const val ARG_NOME_AUDIO = "nome_audio"

        fun newInstance(audioUrl: String, campoInformativo: String, nomeAudio: String): VisualizarAudioPublicoDialogFragment {
            val fragment = VisualizarAudioPublicoDialogFragment()
            val args = Bundle()
            args.putString(ARG_AUDIO_URL, audioUrl)
            args.putString(ARG_CAMPO_TEXTO, campoInformativo)
            args.putString(ARG_NOME_AUDIO, nomeAudio)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_visualizar_audio_publico, container, false)
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

        val tvTextoInformativo = view.findViewById<TextView>(R.id.tvNomeAudio)
        val tvNomeAudio = view.findViewById<TextView>(R.id.tvNomeAudioFirebase)
        val btnPlay = view.findViewById<Button>(R.id.btnPlay)
        val btnPause = view.findViewById<Button>(R.id.btnPause)
        val btnStop = view.findViewById<Button>(R.id.btnStop)

        audioUrl = arguments?.getString(ARG_AUDIO_URL)
        campoInformativo = arguments?.getString(ARG_CAMPO_TEXTO)
        nomeAudio = arguments?.getString(ARG_NOME_AUDIO)

        tvNomeAudio.text = nomeAudio ?: "Áudio desconhecido"

        btnPlay.setOnClickListener {
            if (audioUrl != null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioUrl)
                    setOnPreparedListener { start() }
                    prepareAsync()
                }
            }
        }

        btnPause.setOnClickListener {
            mediaPlayer?.pause()
        }

        btnStop.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}