#include <iostream>
#include <string>
#include <vector>
#include <queue>
#include <iomanip>
#include <cstdlib>
#include <ctime>
using namespace std;

struct ExpressData {
    string expressId;   // 快递单号
    string receiver;    // 收件人姓名
    string pickCode;    // 取件码
    string courier;     // 快递公司代码
    string courierFullName; // 快递公司全名
    bool isPicked;      // 取件状态

    ExpressData(string id, string name, string code, string comp, string compFull) 
        : expressId(id), receiver(name), pickCode(code), courier(comp), 
          courierFullName(compFull), isPicked(false) {}
};

class TreeNode {
public:
    string nodeName;                // 节点名称
    vector<TreeNode*> children;     // 子节点列表
    ExpressData* expressInfo;       // 快递信息指针

    TreeNode(string name) {
        nodeName = name;
        expressInfo = NULL;
    }

    TreeNode(ExpressData* data) {
        nodeName = data->expressId;
        expressInfo = data;
    }

    ~TreeNode() {
        for (int i = 0; i < children.size(); i++) {
            delete children[i];
        }
        delete expressInfo;
    }
};

class ExpressStorageSystem {
public:
    ExpressStorageSystem() {
        root = new TreeNode("学校快递点");
        addFixedCourierStation("南门快递站");
        addFixedCourierStation("北门快递站");
        addFixedCourierStation("西门快递站");
        srand(time(NULL));
    }

    ~ExpressStorageSystem() {
        delete root;
    }

    void addFixedCourierStation(string stationName) {
        TreeNode* stationNode = new TreeNode(stationName);
        root->children.push_back(stationNode);
    }

    bool addExpress(string stationName, string courierCode, string expId, string receiver) {
        TreeNode* stationNode = findChildNode(root, stationName);
        if (stationNode == NULL) {
            cout << "错误：快递站" << stationName << "不存在！" << endl;
            return false;
        }

        string courierName = getCourierFullName(courierCode);
        if (courierName == "未知") {
            cout << "错误：不支持的快递公司！" << endl;
            return false;
        }

        TreeNode* courierNode = findChildNode(stationNode, courierCode);
        if (courierNode == NULL) {
            courierNode = new TreeNode(courierCode);
            stationNode->children.push_back(courierNode);
        }

        if (findChildNode(courierNode, expId) != NULL) {
            cout << "错误：快递单号" << expId << "已存在，添加失败！" << endl;
            return false;
        }

        string pickCode = generatePickCode();
        ExpressData* newExpress = new ExpressData(expId, receiver, pickCode, courierCode, courierName);
        courierNode->children.push_back(new TreeNode(newExpress));
        cout << "\n添加成功！" << endl;
        cout << "快递站点：" << stationName << endl;
        cout << "快递公司：" << courierName << "(" << courierCode << ")" << endl;
        cout << "快递单号：" << expId << endl;
		cout << "收件人：" << receiver << endl;
        cout << "取件码：" << pickCode << endl;
        return true;
    }

    ExpressData* queryExpress(string stationName, string key, bool isReceiver = false) {
        TreeNode* stationNode = findChildNode(root, stationName);
        if (stationNode == NULL) {
            cout << "错误：快递站" << stationName << "不存在！" << endl;
            return NULL;
        }

        queue<TreeNode*> nodeQueue;
        for (int i = 0; i < stationNode->children.size(); i++) {
            nodeQueue.push(stationNode->children[i]);
        }

        bool foundAny = false;
        while (!nodeQueue.empty()) {
            TreeNode* currentNode = nodeQueue.front();
            nodeQueue.pop();

            if (currentNode->expressInfo != NULL) {
                if (isReceiver) {
                    if (currentNode->expressInfo->receiver.find(key) != string::npos) {
                        printExpressDetail(stationName, currentNode->expressInfo);
                        foundAny = true;
                    }
                } else {
                    if (currentNode->expressInfo->pickCode == key) {
                        printExpressDetail(stationName, currentNode->expressInfo);
                        foundAny = true;
                        return currentNode->expressInfo;
                    }
                }
            } else {
                for (int i = 0; i < currentNode->children.size(); i++) {
                    nodeQueue.push(currentNode->children[i]);
                }
            }
        }

        if (!foundAny) {
            string type = isReceiver ? "收件人" : "取件码";
            cout << "\n未找到快递站" << stationName << "中" << type << "为[" << key << "]的快递！" << endl;
        }
        return NULL;
    }

    bool confirmPick(string stationName, string pickCode) {
        ExpressData* target = queryExpress(stationName, pickCode);
        if (target == NULL) {
            return false;
        }

        if (target->isPicked) {
            cout << "\n错误：该快递已取件，不能重复取件！" << endl;
            return false;
        }

        target->isPicked = true;
        cout << "\n取件成功！快递状态已更新为【已取件】" << endl;
        return true;
    }

    void printAllExpress() {
        cout << "\n==================== 学校快递点 ====================" << endl;
        if (root->children.empty()) {
            cout << "当前无快递！" << endl;
            cout << "======================================================" << endl;
            return;
        }

        queue<TreeNode*> nodeQueue;
        nodeQueue.push(root);
        int level = 0;

        while (!nodeQueue.empty()) {
            int levelSize = nodeQueue.size();

            for (int i = 0; i < levelSize; i++) {
                TreeNode* current = nodeQueue.front();
                nodeQueue.pop();

                switch (level) {
                    case 0:
                        cout << "根节点：" << current->nodeName << endl;
                        break;
                    case 1:
                        cout << "  快递站：" << current->nodeName << endl;
                        break;
                    case 2:
                        {
						string fullName = getCourierFullName(current->nodeName);
                        cout << "    快递公司：" << fullName << "(" << current->nodeName << ")" << endl;
                        break;}
                    case 3:
                        cout << "      快递：单号=" << current->expressInfo->expressId
                             << " | 收件人=" << current->expressInfo->receiver
                             << " | 取件码=" << current->expressInfo->pickCode
                             << " | 状态=" << (current->expressInfo->isPicked ? "已取" : "未取") << endl;
                        break;
                }

                for (int j = 0; j < current->children.size(); j++) {
                    nodeQueue.push(current->children[j]);
                }
            }
            level++;
        }
        cout << "======================================================" << endl;
    }

private:
    TreeNode* root;

    TreeNode* findChildNode(TreeNode* parentNode, string targetName) {
        for (int i = 0; i < parentNode->children.size(); i++) {
            if (parentNode->children[i]->nodeName == targetName) {
                return parentNode->children[i];
            }
        }
        return NULL;
    }

    void printExpressDetail(string stationName, ExpressData* express) {
        cout << "\n找到快递，信息如下：" << endl;
        cout << "--------------------------" << endl;
        cout << "快递站点：" << stationName << endl;
        cout << "快递公司：" << express->courierFullName << "(" << express->courier << ")" << endl;
        cout << "快递单号：" << express->expressId << endl;
        cout << "收件人：" << express->receiver << endl;
        cout << "取件码：" << express->pickCode << endl;
        cout << "取件状态：" << (express->isPicked ? "已取件" : "未取件") << endl;
        cout << "--------------------------" << endl;
    }

    string getCourierFullName(string code) {
        if (code == "SF") return "顺丰";
        else if (code == "ZT") return "中通";
        else if (code == "JD") return "京东";
        else if (code == "YD") return "韵达";
        else if (code == "YZ") return "邮政";
        else return "未知";
    }

    string generatePickCode() {
        string code = "";
        const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 6; i++) {
            int index = rand() % chars.size();
            code += chars[index];
        }
        return code;
    }
};

void showMenu() {
    // 移除清屏操作，避免清除历史操作记录
    cout << "==================== 学校快递存储系统 ====================" << endl;
    cout << "1. 添加快递" << endl;
    cout << "2. 查询快递(按取件码)" << endl;
    cout << "3. 查询快递(按收件人)" << endl;
    cout << "4. 确认取件" << endl;
    cout << "5. 打印所有快递" << endl;
    cout << "0. 退出系统" << endl;
    cout << "==============================================================" << endl;
    cout << "请选择功能编号：";
}

string selectCourierStation() {
    cout << "\n请选择快递站点(输入编号)：" << endl;
    cout << "1. 南门快递站" << endl;
    cout << "2. 北门快递站" << endl;
    cout << "3. 西门快递站" << endl;
    cout << "请选择快递站：";
    int choice;
    cin >> choice;
    cin.ignore();
    switch (choice) {
        case 1: return "南门快递站";
        case 2: return "北门快递站";
        case 3: return "西门快递站";
        default: return "";
    }
}

int main() {
    ExpressStorageSystem expressSystem;
    int funcChoice;
    while (true) {
        showMenu();
        cin >> funcChoice;
        cin.ignore();
        switch (funcChoice) {
            case 1: {  // 添加快递
                cout << "\n===== 添加快递 =====" << endl;
                string stationName = selectCourierStation();
                if (stationName.empty()) {
                    cout << "错误：快递站选择无效！" << endl;
                } else {
                    cout << "请输入快递公司代码(大写)："<< endl;
                    cout << "SF=顺丰" << endl;
                    cout << "ZT=中通" << endl;
                    cout << "YD=韵达" << endl;
                    cout << "YZ=邮政" << endl;
                    cout << "JD=京东" << endl;
					cout << "请输入：";  
					string courierCode;
                    getline(cin, courierCode);
                    cout << "请输入快递单号：";
                    string expId;
                    getline(cin, expId);
                    cout << "请输入收件人姓名：";
                    string receiver;
                    getline(cin, receiver);
                    expressSystem.addExpress(stationName, courierCode, expId, receiver);
                }
                // 功能结束提示
                cout << "该功能已结束退出。下一步需要（请输入功能编号）：";
                break;
            }
            case 2: {  // 按取件码查询
                cout << "\n===== 查询快递(按取件码) =====" << endl;
                string stationName = selectCourierStation();
                if (stationName.empty()) {
                    cout << "错误：快递站选择无效！" << endl;
                } else {
                    cout << "请输入取件码：";
                    string pickCode;
                    getline(cin, pickCode);
                    expressSystem.queryExpress(stationName, pickCode, false);
                }
                // 功能结束提示
                cout << "该功能已结束退出。下一步需要（请输入功能编号）：";
                break;
            }
            case 3: {  // 按收件人查询
                cout << "\n===== 查询快递(按收件人) =====" << endl;
                string stationName = selectCourierStation();
                if (stationName.empty()) {
                    cout << "错误：快递站选择无效！" << endl;
                } else {
                    cout << "请输入收件人姓名(支持模糊查询)：";
                    string receiver;
                    getline(cin, receiver);
                    expressSystem.queryExpress(stationName, receiver, true);
                }
                // 功能结束提示
                cout << "该功能已结束退出。下一步需要（请输入功能编号）：";
                break;
            }
            case 4: {  // 确认取件
                cout << "\n===== 确认取件 =====" << endl;
                string stationName = selectCourierStation();
                if (stationName.empty()) {
                    cout << "错误：快递站选择无效！" << endl;
                } else {
                    cout << "请输入取件码：";
                    string pickCode;
                    getline(cin, pickCode);
                    expressSystem.confirmPick(stationName, pickCode);
                }
                // 功能结束提示
                cout << "该功能已结束退出。下一步需要（请输入功能编号）：";
                break;
            }
            case 5: {  // 打印所有快递
                cout << "\n===== 打印所有快递 =====" << endl;
                expressSystem.printAllExpress();
                // 功能结束提示
                cout << "该功能已结束退出。下一步需要（请输入功能编号）：";
                break;
            }
            case 0: {  // 退出系统
                cout << "\n系统已退出，感谢使用！" << endl;
                return 0;
            }
            default: {
                cout << "无效的功能编号，请重新输入！" << endl;
                // 无效选择提示
                cout << "该功能已结束退出。下一步需要（请输入功能编号）：";
                break;
            }
        }
    }
    return 0;
}
