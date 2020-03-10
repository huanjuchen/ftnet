package xyz.huanju.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.huanju.app.storage.SharePath;

import java.io.File;

/**
 * @author HuanJu
 */
@Controller
public class AdminController {

    private SharePath sharePath;

    @Autowired
    public void setSharePath(SharePath sharePath) {
        this.sharePath = sharePath;
    }

    @RequestMapping("/admin")
    public String toAdmin(ModelMap map) {
        map.addAttribute("pathList",sharePath.keys());
        return "admin";
    }

    @RequestMapping("/admin/addPath")
    public String addPath(String path, ModelMap map) {
        String str = path.replace('\\', '/');
        File file = new File(path);
        if (!file.exists()) {
            map.addAttribute("msg", "目录不存在");
            return "/error";
        }
        String key = sharePath.add(str);
        if (key == null) {
            map.addAttribute("msg", "添加错误");
            return "error";
        }
        return "redirect:/admin";
    }

    @RequestMapping("/admin/removePath")
    public String removePath(String key,ModelMap map){
        String str=sharePath.remove(key);
        if (str==null){
            map.addAttribute("msg", "移除失败");
            return "error";
        }
        return "redirect:/admin";
    }


}
