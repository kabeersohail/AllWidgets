package com.chirput.allwidgets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chirput.allwidgets.databinding.FragmentLauncherBinding

class LauncherFragment : Fragment() {

    private lateinit var binding: FragmentLauncherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLauncherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.launcherConstraintLayout.setOnLongClickListener {

            findNavController().navigate(R.id.action_launcherFragment_to_homeFragment)

            return@setOnLongClickListener true
        }

    }

}