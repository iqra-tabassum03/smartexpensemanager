package com.example.smartexpensemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartexpensemanager.databinding.FragmentEditProfileBinding
import com.google.android.material.snackbar.Snackbar

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example: Save button
        binding.btnSaveProfile.setOnClickListener {
            Snackbar.make(binding.root, "Profile updated!", Snackbar.LENGTH_SHORT).show()
            // TODO: Save changes to SharedPreferences or database
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
