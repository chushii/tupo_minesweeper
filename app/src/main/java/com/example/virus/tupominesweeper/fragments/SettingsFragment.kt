package com.example.virus.tupominesweeper.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.virus.tupominesweeper.databinding.FragmentSettingsBinding
import com.example.virus.tupominesweeper.stuff.SettingsPreferences
import com.example.virus.tupominesweeper.stuff.ThemeSwitcher
import com.example.virus.tupominesweeper.viewmodels.SettingsViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val game_settings = SettingsPreferences.loadGameSettings(requireContext())
        val app_settings = SettingsPreferences.loadAppSettings(requireContext())
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val diffs = arrayOf("Легкий", "Средний", "Сложный", "Пользовательский")
        val diff_adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, diffs)
        diff_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.DiffChoice.setAdapter(diff_adapter)
        if (game_settings.diff in 0 until diff_adapter.count) {
            binding.DiffChoice.setSelection(game_settings.diff)
            when (game_settings.diff) {
                0 -> settingsViewModel.setGameSettings(0,9, 9, 10)
                1 -> settingsViewModel.setGameSettings(1,16, 16, 40)
                2 -> settingsViewModel.setGameSettings(2,30, 16, 99)
                else -> settingsViewModel.setGameSettings(3,5, 5, 1)
            }
        }
        binding.DiffChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val rows = when (position) {
                    0 -> 9
                    1 -> 16
                    2 -> 30
                    else -> 5
                }
                val cols = when (position) {
                    0 -> 9
                    1 -> 16
                    2 -> 16
                    else -> 5
                }
                val mines = when (position) {
                    0 -> 10
                    1 -> 40
                    2 -> 99
                    else -> 1
                }
                settingsViewModel.setGameSettings(position, rows, cols, mines)
                SettingsPreferences.saveGameSettings(requireContext(), position, rows, cols, mines)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Ничего не выбрано
            }
        }

        val themes = arrayOf("Системная", "Светлая", "Тёмная")
        val theme_adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, themes)
        theme_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ThemeChoice.setAdapter(theme_adapter)
        if (app_settings.theme in 0 until theme_adapter.count) {
            binding.ThemeChoice.setSelection(app_settings.theme)
        }
        binding.ThemeChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                settingsViewModel.setTheme(position)
                ThemeSwitcher.setTheme(requireContext(), position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Ничего не выбрано
            }
        }

        binding.VibrationSwitch.isChecked = app_settings.vibration
        binding.InverterSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setVibration(isChecked)
            SettingsPreferences.saveVibration(requireContext(), isChecked)
        }

        binding.InverterSwitch.isChecked = app_settings.invert
        binding.InverterSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setInvert(isChecked)
            SettingsPreferences.saveInvert(requireContext(), isChecked)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}