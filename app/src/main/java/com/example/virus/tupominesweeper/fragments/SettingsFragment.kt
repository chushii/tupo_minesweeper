package com.example.virus.tupominesweeper.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.virus.tupominesweeper.R
import com.example.virus.tupominesweeper.databinding.FragmentSettingsBinding
import com.example.virus.tupominesweeper.stuff.SettingsPreferences
import com.example.virus.tupominesweeper.stuff.ThemeSwitcher
import com.example.virus.tupominesweeper.viewmodels.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var lastSelectedPosition = -1

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
        val packageManager = requireContext().packageManager
        val info = packageManager.getPackageInfo(requireContext().packageName, 0)
        val versionName = info.versionName
        val root: View = binding.root

        val diffs = arrayOf("Легкий", "Средний", "Сложный", "Пользовательский")
        val diff_adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, diffs)
        diff_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.DiffChoice.setAdapter(diff_adapter)
        if (game_settings.diff in 0 until diff_adapter.count) {
            val diff = game_settings.diff
            val rows = game_settings.rows
            val cols = game_settings.cols
            val mines = game_settings.mineCount
            binding.DiffChoice.setSelection(diff)
            settingsViewModel.setGameSettings(diff,rows, cols, mines)
        }
        binding.DiffChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == lastSelectedPosition) return
                lastSelectedPosition = position

                if (position == 3) {
                    showCustomDiffDialog()
                }
                else {
                    val rows = when (position) {
                        0 -> 9
                        1 -> 16
                        2 -> 30
                        else -> 9
                    }
                    val cols = when (position) {
                        0 -> 9
                        1 -> 16
                        2 -> 16
                        else -> 9
                    }
                    val mines = when (position) {
                        0 -> 10
                        1 -> 40
                        2 -> 99
                        else -> 10
                    }
                    settingsViewModel.setGameSettings(position, rows, cols, mines)
                    SettingsPreferences.saveGameSettings(requireContext(), position, rows, cols, mines)
                }
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
        binding.VibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setVibration(isChecked)
            SettingsPreferences.saveVibration(requireContext(), isChecked)
        }

        binding.InverterSwitch.isChecked = app_settings.invert
        binding.InverterSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setInvert(isChecked)
            SettingsPreferences.saveInvert(requireContext(), isChecked)
        }

        binding.AppVersion.text = "Версия: v${versionName}"

        return root
    }

    @SuppressLint("SetTextI18n")
    fun showCustomDiffDialog() {
        val context = requireContext()
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        val game_settings = SettingsPreferences.loadGameSettings(context)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_customdiff, null)
        val customRows = dialogView.findViewById<EditText>(R.id.CustomRows)
        val customCols = dialogView.findViewById<EditText>(R.id.CustomCols)
        val customMines = dialogView.findViewById<EditText>(R.id.CustomMines)

        customRows.setText(game_settings.rows.toString())
        customCols.setText(game_settings.cols.toString())
        customMines.setText(game_settings.mineCount.toString())

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Настройки поля")
            .setView(dialogView)
            .setPositiveButton("Принять", null)
            .setNegativeButton("Отмена", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val rowsStr = customRows.text.toString()
                val colsStr = customCols.text.toString()
                val minesStr = customMines.text.toString()

                if (rowsStr.isEmpty() || colsStr.isEmpty() || minesStr.isEmpty()) {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val rows = rowsStr.toIntOrNull()
                val cols = colsStr.toIntOrNull()
                val mines = minesStr.toIntOrNull()

                if (rows == null || cols == null || mines == null) {
                    Toast.makeText(context, "Введите корректные числа", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (rows <= 0 || cols <= 0 || mines <= 0) {
                    Toast.makeText(context, "Числа должны быть больше нуля", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (rows < 5 || cols < 5) {
                    Toast.makeText(context, "Минимальный размер поля: 5х5", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (rows > 30 || cols > 30) {
                    Toast.makeText(context, "Максимальный размер поля: 30х30", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (mines > rows * cols - 9) {
                    val max = rows * cols - 9
                    Toast.makeText(context, "Максимум мин для этого поля: ${max}", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                settingsViewModel.setGameSettings(3, rows, cols, mines)
                SettingsPreferences.saveGameSettings(context, 3, rows, cols, mines)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}