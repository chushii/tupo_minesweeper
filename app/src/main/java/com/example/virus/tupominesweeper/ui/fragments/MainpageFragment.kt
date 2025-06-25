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

    private var lastSettings = SettingsViewModel.GameSettings(20, 20, 10, false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainpageViewModel =
            ViewModelProvider(this).get(MainpageViewModel::class.java)
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentMainpageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val field = binding.FieldView
        val button = binding.ResetButton
        button.setOnClickListener {
            val (rows, cols, mines, invert) = lastSettings
            field.resetGame()
        }

        settingsViewModel.settings.observe(viewLifecycleOwner, { settings ->
            if (!mainpageViewModel.isGameStarted()) {
                mainpageViewModel.startNewGame(settings.rows, settings.cols, settings.mineCount)
            }
            val (rows, cols, mines, invert) = settings
            field.setInvertControls(invert)
            lastSettings = SettingsViewModel.GameSettings(rows, cols, mines, invert)
        })

        mainpageViewModel.gameState.observe(viewLifecycleOwner) { state ->
            field.setupGame(state.rows, state.cols, state.mineCount)
        }

        return root
    }

    private fun MainpageViewModel.isGameStarted(): Boolean {
        return gameState.value != null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}