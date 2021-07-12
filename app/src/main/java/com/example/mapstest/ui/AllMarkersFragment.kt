package com.example.mapstest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.mapstest.adapter.MarkerAdapter
import com.example.mapstest.databinding.FragmentAllMarkersBinding
import com.example.mapstest.viewModels.MapViewModel


class AllMarkersFragment : Fragment() {

    private val viewModel: MapViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAllMarkersBinding.inflate(inflater, container, false)


        val adapter = MarkerAdapter()

        viewModel.markersList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }



        return binding.root
    }
}
