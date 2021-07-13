package com.example.mapstest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.mapstest.R
import com.example.mapstest.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutBinding.inflate(inflater, container, false)

        // navigate to maps fragment when back is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_nav_aboutFragment_to_nav_mapFragment)
        }

        binding.aboutTextTV.text = getString(R.string.about_fragment_text)


        return binding.root
    }

}