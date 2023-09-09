package mx.org.bm.videogamesdb.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.org.bm.videogamesdb.R
import mx.org.bm.videogamesdb.application.VideogamesDBApp
import mx.org.bm.videogamesdb.data.GameRepository
import mx.org.bm.videogamesdb.data.db.model.GameEntity
import mx.org.bm.videogamesdb.databinding.GameDialogBinding
import java.io.IOException

class GameDialog(
    private  var isNewGame: Boolean = true,
    private  var game:GameEntity = GameEntity(
        title = "",
        genre = "",
        genreId = 0,
        developer = ""
    ), private  val updateUI: () -> Unit,
    private  val message: (Int) -> Unit
): DialogFragment() {

    private var _binding: GameDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog

    private  var saveButton: Button? = null

    private lateinit var repository: GameRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = GameDialogBinding.inflate(requireActivity().layoutInflater)

        repository = (requireContext().applicationContext as VideogamesDBApp).repository

        builder = AlertDialog.Builder(requireContext())

        setupSpinnerOptions()

        binding.apply {
            tietTitle.setText(game.title)
            tietDeveloper.setText(game.developer)
            genreSpinner.setSelection(game.genreId)
        }

        val array: Array<String> = resources.getStringArray(R.array.genre_opts)

        dialog = if(isNewGame){
            buildDialog(getString(R.string.save_title), getString(R.string.cancel_title), {
                // Guardar
                game.title = binding.tietTitle.text.toString()
                game.genre = array[binding.genreSpinner.selectedItemPosition]
                game.developer = binding.tietDeveloper.text.toString()
                game.genreId = binding.genreSpinner.selectedItemPosition

                try{
                    lifecycleScope.launch {
                        repository.insertGame(game)

                    }

                    message(R.string.save_message)

                    updateUI()
                }catch(e: IOException){
                    message(R.string.save_error_message)
                }
            }, {
                // Cancelar
            })
        }else{
            buildDialog(getString(R.string.update_title), getString(R.string.delete_title), {
                // Update
                game.title = binding.tietTitle.text.toString()
                game.genre = array[binding.genreSpinner.selectedItemPosition]
                game.developer = binding.tietDeveloper.text.toString()
                game.genreId = binding.genreSpinner.selectedItemPosition

                try{
                    lifecycleScope.launch {
                        repository.updateGame(game)
                    }

                    message(R.string.update_message)

                    updateUI()
                }catch(e: IOException){
                    message(R.string.update_error_message)
                }

            }, {
                // Delete
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm_title))
                    .setMessage(getString(R.string.confirm_message, game.title))
                    .setPositiveButton(getString(R.string.acept_title)){ _, _ ->
                        try{
                            lifecycleScope.launch {
                                repository.deleteGame(game)
                            }

                            message(R.string.delete_message)

                            updateUI()
                        }catch(e: IOException){
                            message(R.string.delete_error_message)
                        }
                    }.setNegativeButton(getString(R.string.cancel_title)){dialog, _ ->
                        dialog.dismiss()
                    }.create()
                    .show()


            })
        }

        return dialog
    }

    private fun setupSpinnerOptions(){
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.genre_opts,
            android.R.layout.simple_spinner_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.genreSpinner.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        val alertDialog = dialog as AlertDialog
        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = validateFields()

        binding.tietTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tietDeveloper.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })
    }

    private fun validateFields(): Boolean{
        return (binding.tietTitle.text.toString().isNotEmpty() && binding.tietDeveloper.text.toString().isNotEmpty())
    }

    private fun buildDialog(btn1Text: String, btn2Text:String,
                            positiveButton: () -> Unit, negativeButton: () -> Unit): Dialog =
        builder.setView(binding.root)
            .setTitle(getString(R.string.game_title))
            .setPositiveButton(btn1Text){dialog, _ ->
                positiveButton()
            }
            .setNegativeButton(btn2Text){dialog, _ ->
                negativeButton()
            }.create()
}