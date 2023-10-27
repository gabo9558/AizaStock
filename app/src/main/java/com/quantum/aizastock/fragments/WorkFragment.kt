package com.quantum.aizastock.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.quantum.aizastock.R
import com.quantum.aizastock.common.PreferenceUtils
import com.quantum.aizastock.databinding.WorkFragmentBinding

class WorkFragment: Fragment() {

    private var _binding: WorkFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bundle: Bundle
    private var group_id: Int = 0
    private var current_amount: Int = 0
    private var total_amount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WorkFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Functions
        arguments?.let { bundle = it }
        setListeners()
        initData()
    }

    private fun initData(){
        arguments?.let {
            PreferenceUtils.saveCurrentName(requireContext(), it.getString("group_name") ?: "")
            PreferenceUtils.saveTotalAmount(requireContext(), it.getInt("group_amount"))
            PreferenceUtils.saveCurrentID(requireContext(), it.getInt("id_group"))
        }

        with(binding){
            tvGroupName.text = PreferenceUtils.getCurrentName(requireContext())
            tvTotalAmount.text = PreferenceUtils.getTotalAmount(requireContext()).toString()
            tvCurrentAmount.text = PreferenceUtils.getCurrentAmount(requireContext()).toString()
        }
    }

    private fun setListeners(){
        with(binding){
            view?.let {
                btnCheckItem.setOnClickListener {
                    view?.let {
                        Navigation.findNavController(it).navigate(R.id.work_to_fragment_scan)
                    }
                }

                btnManualAdd.setOnClickListener {
                    view?.let {
                        Navigation.findNavController(it).navigate(R.id.work_to_manual_add)
                    }
                }

                btnFinishWork.setOnClickListener {
                    popValidateExit()
                }
            }
        }
    }

    private fun popValidateExit(){
        AlertDialog.Builder(requireContext())
            .setMessage("¿Estas seguro de terminar la tarea?")
            .setPositiveButton("Terminar") { _, _ ->
                PreferenceUtils.saveTotalAmount(requireContext(), 0)
                PreferenceUtils.saveCurrentAmount(requireContext(), 0)

                view?.let { Navigation.findNavController(it).navigate(R.id.work_to_home) }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }
}