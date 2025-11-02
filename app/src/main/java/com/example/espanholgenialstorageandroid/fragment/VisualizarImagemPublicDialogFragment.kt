package com.example.espanholgenialstorageandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.espanholgenialstorageandroid.R

class VisualizarImagemPublicDialogFragment : DialogFragment()
{
    companion object {
        private const val ARG_IMAGE_URL = "image_url"
        private const val ARG_CAMPO_TEXTO = "campo_texto"
        private const val ARG_IMAGE_NAME_FIREBASE = "image_name_firebase"

        fun newInstance(imageUrl: String, campoTexto: String, imageNameFirebase: String): VisualizarImagemPublicDialogFragment {
            val fragment = VisualizarImagemPublicDialogFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            args.putString(ARG_CAMPO_TEXTO, campoTexto)
            args.putString(ARG_IMAGE_NAME_FIREBASE, imageNameFirebase)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar layout normalmente
        return inflater.inflate(R.layout.dialog_visualizar_imagem_publica, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // Fundo transparente
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view.findViewById<ImageView>(R.id.ivDialogImagem)
        val tvCampoInformativo = view.findViewById<TextView>(R.id.tvCampoInformativo)
        val tvNomeImagemFirebase = view.findViewById<TextView>(R.id.tvNomeImagemFirebase)

        val imageUrl = arguments?.getString(VisualizarImagemPublicDialogFragment.ARG_IMAGE_URL)
        val campoTexto = arguments?.getString(VisualizarImagemPublicDialogFragment.ARG_CAMPO_TEXTO)
        val imageNameFirebase = arguments?.getString(VisualizarImagemPublicDialogFragment.ARG_IMAGE_NAME_FIREBASE)

        tvCampoInformativo.setText("Nome da imagem:")
        tvNomeImagemFirebase.text = imageNameFirebase ?: "Desconhecido"

        // Carregar a imagem com Glide
        imageUrl?.let {
            Glide.with(this)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    }
}