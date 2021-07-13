package com.example.mapstest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.mapstest.R
import com.example.mapstest.adapter.MarkerAdapter
import com.example.mapstest.adapter.OnClickListener
import com.example.mapstest.databinding.FragmentAllMarkersBinding
import com.example.mapstest.db.MarkerEntity
import com.example.mapstest.viewModels.MapViewModel
import com.google.android.gms.maps.model.LatLng


class AllMarkersFragment : Fragment() {

    private val viewModel: MapViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAllMarkersBinding.inflate(inflater, container, false)

        // navigate to maps fragment when back is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_nav_allMarkersFragment_to_nav_mapFragment)
        }

        val adapter = MarkerAdapter(object : OnClickListener {
            override fun onItemClicked(marker: MarkerEntity) {
                val action =
                    AllMarkersFragmentDirections.actionNavAllMarkersFragmentToNavMapFragment(
                        isArgumentPassed = true,
                        lat = marker.latitude.toFloat(),
                        lng = marker.longitude.toFloat()
                    )
                findNavController().navigate(action)
            }
        })

        viewModel.markersList.observe(viewLifecycleOwner) {
            binding.markersEmptyStateTv.visibility = if (it.isNullOrEmpty()) View.VISIBLE
            else View.GONE

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
