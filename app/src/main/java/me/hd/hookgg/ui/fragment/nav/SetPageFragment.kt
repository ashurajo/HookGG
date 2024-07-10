package me.hd.hookgg.ui.fragment.nav

import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import me.hd.hookgg.MyApp
import me.hd.hookgg.R
import me.hd.hookgg.data.AppData
import me.hd.hookgg.data.SetPrefsData
import me.hd.hookgg.databinding.DialogEditPackageNameBinding
import me.hd.hookgg.databinding.FragmentSetPageBinding
import me.hd.hookgg.ui.fragment.base.BaseFragment

class SetPageFragment : BaseFragment<FragmentSetPageBinding, ViewModel>(
    FragmentSetPageBinding::inflate,
    null
) {
    override fun initFragment(binding: FragmentSetPageBinding, viewModel: ViewModel?) {
        initView(binding)
    }

    private fun initView(binding: FragmentSetPageBinding) {
        if (YukiHookAPI.Status.isModuleActive) {
            binding.setPageToolbar.subtitle = getString(R.string.module_active)
        }
        initPrefsDefVal(binding)
        initPrefsOnClick(binding)
    }

    private fun initPrefsDefVal(binding: FragmentSetPageBinding) {
        val prefs = MyApp.context.prefs()
        binding.setSwitchTestFunc.isChecked = prefs.get(SetPrefsData.TEST_FUNC)
        binding.setTvDefPackageName.text = prefs.get(SetPrefsData.PACKAGE_NAME)
        val versionName = prefs.get(SetPrefsData.VERSION_NAME)
        binding.setTvDefVersionName.text = versionName
        binding.setTvDefFunctionList.text = getString(R.string.prefs_def_function_num)
            .format(
                prefs.get(SetPrefsData.FUNCTION_LIST).size,
                AppData.getFunctionList(versionName).size
            )
        binding.setSwitchLogReturn.isChecked = prefs.get(SetPrefsData.LOG_RETURN)
        binding.setSwitchFilterParams.isChecked = prefs.get(SetPrefsData.FILTER_PARAMS)
    }

    private fun initPrefsOnClick(binding: FragmentSetPageBinding) {
        val prefs = MyApp.context.prefs()
        binding.setSwitchTestFunc.setOnClickListener {
            val oldTestFunc = prefs.get(SetPrefsData.TEST_FUNC)
            val newTestFunc = !oldTestFunc
            prefs.edit {
                put(SetPrefsData.TEST_FUNC, newTestFunc)
            }
            binding.setSwitchTestFunc.isChecked = newTestFunc
        }
        binding.setLLPackageName.setOnClickListener {
            val oldPackageName = prefs.get(SetPrefsData.PACKAGE_NAME)
            val dialogBinding =
                DialogEditPackageNameBinding.inflate(LayoutInflater.from(requireContext()))
            dialogBinding.textInputEditText.setText(oldPackageName)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.prefs_key_package_name)
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.dialog_accept) { _, _ ->
                    val newPackageName = dialogBinding.textInputEditText.text.toString()
                    if (newPackageName.isNotEmpty()) {
                        prefs.edit {
                            put(SetPrefsData.PACKAGE_NAME, newPackageName)
                        }
                    }
                    binding.setTvDefPackageName.text = newPackageName
                }
                .setNegativeButton(R.string.dialog_decline) { _, _ -> }
                .show()
        }
        binding.setLLVersionName.setOnClickListener {
            val versionList = AppData.getVersionList()
            val oldVersionName = prefs.get(SetPrefsData.VERSION_NAME)
            val oldVersionIndex = versionList.indexOf(oldVersionName)
            var newVersionIndex = oldVersionIndex
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.prefs_key_version_name)
                .setSingleChoiceItems(versionList, oldVersionIndex) { _, which ->
                    newVersionIndex = which
                }
                .setPositiveButton(R.string.dialog_accept) { _, _ ->
                    val newVersionName = versionList[newVersionIndex]
                    val newFunctionListSet = setOf<String>()
                    prefs.edit {
                        put(SetPrefsData.VERSION_NAME, newVersionName)
                        put(SetPrefsData.FUNCTION_LIST, newFunctionListSet)
                    }
                    binding.setTvDefVersionName.text = newVersionName
                    binding.setTvDefFunctionList.text =
                        getString(R.string.prefs_def_function_num).format(
                            0,
                            AppData.getFunctionList(newVersionName).size
                        )
                }
                .setNegativeButton(R.string.dialog_decline) { _, _ -> }
                .show()
        }
        binding.setLLFunctionList.setOnClickListener {
            val versionName = prefs.get(SetPrefsData.VERSION_NAME)
            val functionList = AppData.getFunctionList(versionName)
            val oldFunctionList = prefs.get(SetPrefsData.FUNCTION_LIST)
            val oldFunctionStatus = BooleanArray(functionList.size) {
                functionList[it] in oldFunctionList
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.prefs_key_function_list)
                .setMultiChoiceItems(functionList, oldFunctionStatus) { _, which, isChecked ->
                    oldFunctionStatus[which] = isChecked
                }
                .setPositiveButton(R.string.dialog_accept) { _, _ ->
                    val newFunctionListSet = functionList.filterIndexed { index, _ ->
                        oldFunctionStatus[index]
                    }.toSet()
                    prefs.edit {
                        put(SetPrefsData.FUNCTION_LIST, newFunctionListSet)
                    }
                    binding.setTvDefFunctionList.text =
                        getString(R.string.prefs_def_function_num).format(
                            newFunctionListSet.size,
                            functionList.size
                        )
                }
                .setNegativeButton(R.string.dialog_decline) { _, _ -> }
                .show()
        }
        binding.setSwitchLogReturn.setOnClickListener {
            val oldLogReturn = prefs.get(SetPrefsData.LOG_RETURN)
            val newLogReturn = !oldLogReturn
            prefs.edit {
                put(SetPrefsData.LOG_RETURN, newLogReturn)
            }
            binding.setSwitchLogReturn.isChecked = newLogReturn
        }
        binding.setSwitchFilterParams.setOnClickListener {
            val oldFilterParams = prefs.get(SetPrefsData.FILTER_PARAMS)
            val newFilterParams = !oldFilterParams
            prefs.edit {
                put(SetPrefsData.FILTER_PARAMS, newFilterParams)
            }
            binding.setSwitchFilterParams.isChecked = newFilterParams
        }
    }
}