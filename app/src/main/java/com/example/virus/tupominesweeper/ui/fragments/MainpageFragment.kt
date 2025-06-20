package com.example.virus.tupominesweeper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.virus.tupominesweeper.databinding.FragmentMainpageBinding

class MainpageFragment : Fragment() {

    private var _binding: FragmentMainpageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainpageViewModel =
            ViewModelProvider(this).get(MainpageViewModel::class.java)
        _binding = FragmentMainpageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textMainpage.text = "Главный экран"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}