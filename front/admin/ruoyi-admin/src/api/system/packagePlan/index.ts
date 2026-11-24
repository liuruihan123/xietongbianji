import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { PackagePlanVO, PackagePlanForm, PackagePlanQuery } from '@/api/system/packagePlan/types';

/**
 * 查询套餐管理列表
 * @param query
 * @returns {*}
 */

export const listPackagePlan = (query?: PackagePlanQuery): AxiosPromise<PackagePlanVO[]> => {
  return request({
    url: '/system/packagePlan/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询套餐管理详细
 * @param id
 */
export const getPackagePlan = (id: string | number): AxiosPromise<PackagePlanVO> => {
  return request({
    url: '/system/packagePlan/' + id,
    method: 'get'
  });
};

/**
 * 新增套餐管理
 * @param data
 */
export const addPackagePlan = (data: PackagePlanForm) => {
  return request({
    url: '/system/packagePlan',
    method: 'post',
    data: data
  });
};

/**
 * 修改套餐管理
 * @param data
 */
export const updatePackagePlan = (data: PackagePlanForm) => {
  return request({
    url: '/system/packagePlan',
    method: 'put',
    data: data
  });
};

/**
 * 删除套餐管理
 * @param id
 */
export const delPackagePlan = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/packagePlan/' + id,
    method: 'delete'
  });
};
