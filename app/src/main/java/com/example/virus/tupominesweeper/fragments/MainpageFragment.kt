package com.example.virus.tupominesweeper.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.virus.tupominesweeper.databinding.FragmentMainpageBinding
import com.example.virus.tupominesweeper.stuff.GameStatePreferences
import com.example.virus.tupominesweeper.stuff.SettingsPreferences
import com.example.virus.tupominesweeper.viewmodels.MainpageViewModel
import com.example.virus.tupominesweeper.viewmodels.SettingsViewModel

class MainpageFragment : Fragment() {

    private var _binding: FragmentMainpageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val savedState = GameStatePreferences.loadGameState(requireContext())
        val game_settings = SettingsPreferences.loadGameSettings(requireContext())
        val mainpageViewModel =
            ViewModelProvider(this).get(MainpageViewModel::class.java)
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentMainpageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val field = binding.FieldView
        val button = binding.ResetButton
        button.setOnClickListener {
            field.resetGame(game_settings)
        }

        field.setViewModel(mainpageViewModel)

        settingsViewModel.app_settings.observe(viewLifecycleOwner, { settings ->
            field.setInvertControls(settings.invert)
        })

        if (mainpageViewModel.game_state.value != null) {
            field.restoreGame(mainpageViewModel.game_state.value!!)
        } else if (savedState != null) {
            mainpageViewModel.loadGameState(savedState)
            field.restoreGame(savedState)
        } else {
            mainpageViewModel.resetGame(game_settings.rows, game_settings.cols, game_settings.mineCount)
            field.resetGame(game_settings)
        }

        mainpageViewModel.game_state.observe(viewLifecycleOwner) { state ->
            GameStatePreferences.saveGameState(requireContext(), state)
        }

        return root
    }

    private fun MainpageViewModel.isGameStarted(): Boolean {
        return game_state.value != null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}