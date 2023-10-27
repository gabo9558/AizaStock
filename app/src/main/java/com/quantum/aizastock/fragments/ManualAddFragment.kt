package com.quantum.aizastock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.quantum.aizastock.R
import com.quantum.aizastock.databinding.ManualAddFragmentBinding

class ManualAddFragment: Fragment() {

    private var _binding: ManualAddFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ManualAddFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners(){
        binding.btnFinish.setOnClickListener {
            val bundle = Bundle()
            bundle.apply {
                putString("IMEI", binding.etImeiManual.text.toString())
            }
            view?.let {
                Navigation.findNavController(it).navigate(R.id.manual_to_description, bundle)
            }
        }
    }
}