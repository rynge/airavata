/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef applicationDeploymentModel_TYPES_H
#define applicationDeploymentModel_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <thrift/cxxfunctional.h>





class SetEnvPaths {
 public:

  static const char* ascii_fingerprint; // = "07A9615F837F7D0A952B595DD3020972";
  static const uint8_t binary_fingerprint[16]; // = {0x07,0xA9,0x61,0x5F,0x83,0x7F,0x7D,0x0A,0x95,0x2B,0x59,0x5D,0xD3,0x02,0x09,0x72};

  SetEnvPaths() : name(), value() {
  }

  virtual ~SetEnvPaths() throw() {}

  std::string name;
  std::string value;

  void __set_name(const std::string& val) {
    name = val;
  }

  void __set_value(const std::string& val) {
    value = val;
  }

  bool operator == (const SetEnvPaths & rhs) const
  {
    if (!(name == rhs.name))
      return false;
    if (!(value == rhs.value))
      return false;
    return true;
  }
  bool operator != (const SetEnvPaths &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const SetEnvPaths & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(SetEnvPaths &a, SetEnvPaths &b);

typedef struct _ApplicationModule__isset {
  _ApplicationModule__isset() : appModuleVersion(false), appModuleDescription(false) {}
  bool appModuleVersion;
  bool appModuleDescription;
} _ApplicationModule__isset;

class ApplicationModule {
 public:

  static const char* ascii_fingerprint; // = "EAD3BFBDF5BD64DF63F11230D11B43DE";
  static const uint8_t binary_fingerprint[16]; // = {0xEA,0xD3,0xBF,0xBD,0xF5,0xBD,0x64,0xDF,0x63,0xF1,0x12,0x30,0xD1,0x1B,0x43,0xDE};

  ApplicationModule() : isEmpty(false), appModuleId("DO_NOT_SET_AT_CLIENTS"), appModuleName(), appModuleVersion(), appModuleDescription() {
  }

  virtual ~ApplicationModule() throw() {}

  bool isEmpty;
  std::string appModuleId;
  std::string appModuleName;
  std::string appModuleVersion;
  std::string appModuleDescription;

  _ApplicationModule__isset __isset;

  void __set_isEmpty(const bool val) {
    isEmpty = val;
  }

  void __set_appModuleId(const std::string& val) {
    appModuleId = val;
  }

  void __set_appModuleName(const std::string& val) {
    appModuleName = val;
  }

  void __set_appModuleVersion(const std::string& val) {
    appModuleVersion = val;
    __isset.appModuleVersion = true;
  }

  void __set_appModuleDescription(const std::string& val) {
    appModuleDescription = val;
    __isset.appModuleDescription = true;
  }

  bool operator == (const ApplicationModule & rhs) const
  {
    if (!(isEmpty == rhs.isEmpty))
      return false;
    if (!(appModuleId == rhs.appModuleId))
      return false;
    if (!(appModuleName == rhs.appModuleName))
      return false;
    if (__isset.appModuleVersion != rhs.__isset.appModuleVersion)
      return false;
    else if (__isset.appModuleVersion && !(appModuleVersion == rhs.appModuleVersion))
      return false;
    if (__isset.appModuleDescription != rhs.__isset.appModuleDescription)
      return false;
    else if (__isset.appModuleDescription && !(appModuleDescription == rhs.appModuleDescription))
      return false;
    return true;
  }
  bool operator != (const ApplicationModule &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const ApplicationModule & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(ApplicationModule &a, ApplicationModule &b);

typedef struct _ApplicationDeploymentDescription__isset {
  _ApplicationDeploymentDescription__isset() : appDeploymentDescription(false), moduleLoadCmd(false), libPrependPaths(false), libAppendPaths(false), setEnvironment(false) {}
  bool appDeploymentDescription;
  bool moduleLoadCmd;
  bool libPrependPaths;
  bool libAppendPaths;
  bool setEnvironment;
} _ApplicationDeploymentDescription__isset;

class ApplicationDeploymentDescription {
 public:

  static const char* ascii_fingerprint; // = "50C12617CDB84220D07D0920711332CF";
  static const uint8_t binary_fingerprint[16]; // = {0x50,0xC1,0x26,0x17,0xCD,0xB8,0x42,0x20,0xD0,0x7D,0x09,0x20,0x71,0x13,0x32,0xCF};

  ApplicationDeploymentDescription() : isEmpty(false), appDeploymentId("DO_NOT_SET_AT_CLIENTS"), appModuleId(), computeHostId(), executablePath(), appDeploymentDescription(), moduleLoadCmd() {
  }

  virtual ~ApplicationDeploymentDescription() throw() {}

  bool isEmpty;
  std::string appDeploymentId;
  std::string appModuleId;
  std::string computeHostId;
  std::string executablePath;
  std::string appDeploymentDescription;
  std::string moduleLoadCmd;
  std::vector<SetEnvPaths>  libPrependPaths;
  std::vector<SetEnvPaths>  libAppendPaths;
  std::vector<SetEnvPaths>  setEnvironment;

  _ApplicationDeploymentDescription__isset __isset;

  void __set_isEmpty(const bool val) {
    isEmpty = val;
  }

  void __set_appDeploymentId(const std::string& val) {
    appDeploymentId = val;
  }

  void __set_appModuleId(const std::string& val) {
    appModuleId = val;
  }

  void __set_computeHostId(const std::string& val) {
    computeHostId = val;
  }

  void __set_executablePath(const std::string& val) {
    executablePath = val;
  }

  void __set_appDeploymentDescription(const std::string& val) {
    appDeploymentDescription = val;
    __isset.appDeploymentDescription = true;
  }

  void __set_moduleLoadCmd(const std::string& val) {
    moduleLoadCmd = val;
    __isset.moduleLoadCmd = true;
  }

  void __set_libPrependPaths(const std::vector<SetEnvPaths> & val) {
    libPrependPaths = val;
    __isset.libPrependPaths = true;
  }

  void __set_libAppendPaths(const std::vector<SetEnvPaths> & val) {
    libAppendPaths = val;
    __isset.libAppendPaths = true;
  }

  void __set_setEnvironment(const std::vector<SetEnvPaths> & val) {
    setEnvironment = val;
    __isset.setEnvironment = true;
  }

  bool operator == (const ApplicationDeploymentDescription & rhs) const
  {
    if (!(isEmpty == rhs.isEmpty))
      return false;
    if (!(appDeploymentId == rhs.appDeploymentId))
      return false;
    if (!(appModuleId == rhs.appModuleId))
      return false;
    if (!(computeHostId == rhs.computeHostId))
      return false;
    if (!(executablePath == rhs.executablePath))
      return false;
    if (__isset.appDeploymentDescription != rhs.__isset.appDeploymentDescription)
      return false;
    else if (__isset.appDeploymentDescription && !(appDeploymentDescription == rhs.appDeploymentDescription))
      return false;
    if (__isset.moduleLoadCmd != rhs.__isset.moduleLoadCmd)
      return false;
    else if (__isset.moduleLoadCmd && !(moduleLoadCmd == rhs.moduleLoadCmd))
      return false;
    if (__isset.libPrependPaths != rhs.__isset.libPrependPaths)
      return false;
    else if (__isset.libPrependPaths && !(libPrependPaths == rhs.libPrependPaths))
      return false;
    if (__isset.libAppendPaths != rhs.__isset.libAppendPaths)
      return false;
    else if (__isset.libAppendPaths && !(libAppendPaths == rhs.libAppendPaths))
      return false;
    if (__isset.setEnvironment != rhs.__isset.setEnvironment)
      return false;
    else if (__isset.setEnvironment && !(setEnvironment == rhs.setEnvironment))
      return false;
    return true;
  }
  bool operator != (const ApplicationDeploymentDescription &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const ApplicationDeploymentDescription & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(ApplicationDeploymentDescription &a, ApplicationDeploymentDescription &b);



#endif
