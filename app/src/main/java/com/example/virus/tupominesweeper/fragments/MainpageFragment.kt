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
import com.example.virus.tupominesweeper.viewmodels.GameTimerViewModel
import com.example.virus.tupominesweeper.viewmodels.MainpageViewModel

class MainpageFragment : Fragment() {

    private var _binding: FragmentMainpageBinding? = null
    private val binding get() = _binding!!
    private lateinit var gameTimerViewModel: GameTimerViewModel

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val seconds = (totalSeconds % 60).toInt()
        val minutes = ((totalSeconds / 60) % 60).toInt()
        val hours = (totalSeconds / 3600).toInt()
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val savedState = GameStatePreferences.loadGameState(requireContext())
        val game_settings = SettingsPreferences.loadGameSettings(requireContext())
        val mainpageViewModel =
            ViewModelProvider(this).get(MainpageViewModel::class.java)
        gameTimerViewModel =
            ViewModelProvider(this).get(GameTimerViewModel::class.java)
        _binding = FragmentMainpageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val field = binding.FieldView
        val button = binding.ResetButton
        button.setOnClickListener {
            field.resetGame(game_settings)
            gameTimerViewModel.reset()
            gameTimerViewModel.start(0)
        }

        field.setViewModel(mainpageViewModel)

        val currentState = mainpageViewModel.game_state
        if (currentState.value != null) {
            field.restoreGame(currentState.value!!)
            if (!mainpageViewModel.gameEnded)
                gameTimerViewModel.start(gameTimerViewModel.elapsedTime.value ?: 0)
        } else if (savedState != null) {
            mainpageViewModel.loadGameState(savedState)
            field.restoreGame(savedState)
            if (!mainpageViewModel.gameEnded)
                gameTimerViewModel.start(GameStatePreferences.loadTimeSpent(requireContext()))
        } else {
            mainpageViewModel.resetGame(game_settings.diff, game_settings.rows, game_settings.cols, game_settings.mineCount)
            field.resetGame(game_settings)
            gameTimerViewModel.reset()
            gameTimerViewModel.start(0)
        }

        mainpageViewModel.game_state.observe(viewLifecycleOwner) { state ->
            if (state!!.gameEnded) {
                gameTimerViewModel.pause()
            }
            GameStatePreferences.saveGameState(requireContext(), state)
            GameStatePreferences.saveTimeSpent(requireContext(), gameTimerViewModel.elapsedTime.value ?: 0)
            binding.Counter.text = mainpageViewModel.getMinesCounter(state.cells, state.mineCount).toString()
        }

        gameTimerViewModel.elapsedTime.observe(viewLifecycleOwner) { time ->
            binding.Time.text = formatTime(time)
            GameStatePreferences.saveTimeSpent(requireContext(), gameTimerViewModel.elapsedTime.value ?: 0)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        val mainpageViewModel =
            ViewModelProvider(this).get(MainpageViewModel::class.java)
        if (!mainpageViewModel.gameEnded)
            gameTimerViewModel.start(gameTimerViewModel.elapsedTime.value ?: 0)
    }

    override fun onPause() {
        super.onPause()
        gameTimerViewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        GameStatePreferences.saveTimeSpent(requireContext(), gameTimerViewModel.elapsedTime.value ?: 0)
        _binding = null
    }
}